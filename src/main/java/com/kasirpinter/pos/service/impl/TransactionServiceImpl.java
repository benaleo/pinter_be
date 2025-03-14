package com.kasirpinter.pos.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kasirpinter.pos.entity.Member;
import com.kasirpinter.pos.entity.Product;
import com.kasirpinter.pos.entity.RlUserShift;
import com.kasirpinter.pos.entity.Transaction;
import com.kasirpinter.pos.entity.TransactionProduct;
import com.kasirpinter.pos.entity.TransactionProductId;
import com.kasirpinter.pos.entity.Users;
import com.kasirpinter.pos.enums.TransactionStatus;
import com.kasirpinter.pos.model.TransactionModel;
import com.kasirpinter.pos.model.search.ListOfFilterPagination;
import com.kasirpinter.pos.model.search.SavedKeywordAndPageable;
import com.kasirpinter.pos.repository.MemberRepository;
import com.kasirpinter.pos.repository.ProductRepository;
import com.kasirpinter.pos.repository.RlUserShiftRepository;
import com.kasirpinter.pos.repository.TransactionProductRepository;
import com.kasirpinter.pos.repository.TransactionRepository;
import com.kasirpinter.pos.repository.UserRepository;
import com.kasirpinter.pos.response.PageCreateReturn;
import com.kasirpinter.pos.response.ResultPageResponseDTO;
import com.kasirpinter.pos.service.TransactionService;
import com.kasirpinter.pos.util.ContextPrincipal;
import com.kasirpinter.pos.util.GlobalConverter;
import com.kasirpinter.pos.util.TreeGetEntity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private final UserRepository userRepository;

    private final ProductRepository productRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionProductRepository transactionProductRepository;
    private final MemberRepository memberRepository;

    private final RlUserShiftRepository userShiftRepository;

    @Override
    public ResultPageResponseDTO<TransactionModel.TransactionIndexResponse> findDataIndex(Integer pages, Integer limit, String sortBy, String direction, String keyword) {

        ListOfFilterPagination filter = new ListOfFilterPagination(keyword);
        SavedKeywordAndPageable set = GlobalConverter.appsCreatePageable(pages, limit, sortBy, direction, keyword, filter);

        // First page result (get total count)
        Page<Transaction> firstResult = transactionRepository.findDataByKeyword(set.keyword(), set.pageable());

        // Use a correct Pageable for fetching the next page
        Pageable pageable = GlobalConverter.oldSetPageable(pages, limit, sortBy, direction, firstResult, null);
        Page<Transaction> pageResult = transactionRepository.findDataByKeyword(set.keyword(), pageable);

        // Map the data to the DTOs
        List<TransactionModel.TransactionIndexResponse> dtos = pageResult.stream().map(this::convertToBackResponse).collect(Collectors.toList());

        return PageCreateReturn.create(
                pageResult,
                dtos
        );
    }

    @Override
    public TransactionModel.TransactionDetailResponse findDataById(String id) {
        Transaction data = TreeGetEntity.parsingTransactionByProjection(id, transactionRepository);

        List<TransactionModel.TransactionItem> listTransactions = new ArrayList<>();
        for (TransactionProduct item : data.getListTransaction()) {
            listTransactions.add(new TransactionModel.TransactionItem(
                    item.getProduct().getName(),
                    item.getProduct().getPrice(),
                    item.getQuantity()
            ));
        }

        return new TransactionModel.TransactionDetailResponse(
                data.getInvoice(),
                data.getAmountPayment(),
                data.getCustomerName(),
                data.getTypePayment(),
                data.getCashierName(),
                data.getStoreName(),
                data.getStatus(),
                listTransactions
        );
    }

    @Override
    @Transactional
    public TransactionModel.TransactionIndexResponse saveData(TransactionModel.TransactionCreateUpdateRequest dto) {
        Long userId = ContextPrincipal.getId();
        String userSecureId = ContextPrincipal.getSecureUserId();
        Users user = TreeGetEntity.parsingUserByProjection(userSecureId, userRepository);
        LocalDateTime now = LocalDateTime.now();
        String nowFormat = now.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

        RlUserShift userShift = userShiftRepository.findByUserAndDate(user, now.toLocalDate()).orElse(null);

        if (userShift == null){
            throw new RuntimeException("Kamu harus absen kedalam shift terlebih dahulu");
        }

        String randomString6Char = GlobalConverter.generateRandomString(6);

        Transaction newData = new Transaction();
        newData.setInvoice("INV/" + user.getCompany().getCode() + "/" + nowFormat + "/" + randomString6Char);
        newData.setAmountPayment(dto.getAmountPayment());
        newData.setTypePayment(dto.getTypePayment());
        newData.setCashierName(user.getName());
        newData.setStoreName(user.getCompany().getName());
        newData.setUserShift(userShift);
        newData.setCompany(user.getCompany());
        newData.setStatus(dto.getStatus());

        // set member
        Member optionalMember = memberRepository.findByPhone(dto.getCustomerName()).orElse(null);
        newData.setMember(optionalMember);
        newData.setCustomerName(optionalMember != null ? optionalMember.getName() : dto.getCustomerName());

        GlobalConverter.CmsAdminCreateAtBy(newData, userId);
        Transaction savedData = transactionRepository.save(newData);

        // add list product transaction
        convertTransactionProduct(dto, savedData);

        // decrease the stock product
        convertToDecreaseStockInPaid(savedData);

        // return
        return convertToBackResponse(savedData);
    }

    @Override
    @Transactional
    public TransactionModel.TransactionIndexResponse updateData(String id, TransactionModel.TransactionCreateUpdateRequest dto) {
        Long userId = ContextPrincipal.getId();

        Transaction data = TreeGetEntity.parsingTransactionByProjection(id, transactionRepository);

        // stop if transaction already paid
        if (data.getStatus().equals(TransactionStatus.PAID)) {
            throw new RuntimeException("Transaction already paid");
        }

        data.setAmountPayment(dto.getAmountPayment());
        data.setTypePayment(dto.getTypePayment());
        data.setStatus(dto.getStatus());

        GlobalConverter.CmsAdminUpdateAtBy(data, userId);
        Transaction savedData = transactionRepository.save(data);

        // remove first
        transactionProductRepository.deleteByTransaction(savedData);

        // add list product transaction
        convertTransactionProduct(dto, savedData);

        // decrease the stock product
        if (!data.getStatus().equals(TransactionStatus.PENDING) && savedData.getStatus().equals(TransactionStatus.PAID)) {
            convertToDecreaseStockInPaid(savedData);
        }

        // return
        return convertToBackResponse(savedData);
    }

    @Override
    @Transactional
    public void deleteData(String id) {
        Transaction data = TreeGetEntity.parsingTransactionByProjection(id, transactionRepository);
        transactionRepository.delete(data);
    }

    @Override
    @Transactional
    public TransactionModel.TransactionIndexResponse updateStatusToCancel(String id) {
        Transaction data = TreeGetEntity.parsingTransactionByProjection(id, transactionRepository);
        List<TransactionProduct> products = transactionProductRepository.findAllByTransaction(data);

        for (TransactionProduct item : products) {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
            productRepository.save(product);
        }

        transactionRepository.updateStatusTransaction(data, TransactionStatus.CANCELLED);

        return convertToBackResponse(data);
    }

    private TransactionModel.TransactionIndexResponse convertToBackResponse(Transaction c) {
        TransactionModel.TransactionIndexResponse dto = new TransactionModel.TransactionIndexResponse();
        dto.setInvoice(c.getInvoice());
        dto.setTotalPayment(c.totalPayment());
        dto.setAmountPayment(c.getAmountPayment());
        dto.setReturnPayment(c.getAmountPayment() - c.totalPayment());
        dto.setTypePayment(c.getTypePayment().name());
        dto.setCashierName(c.getCashierName());
        dto.setStoreName(c.getStoreName());
        dto.setStatus(c.getStatus().name());
        dto.setCustomerName(c.getCustomerName());

        GlobalConverter.CmsIDTimeStampResponseAndId(dto, c, userRepository);
        return dto;
    }

    private void convertToDecreaseStockInPaid(Transaction data) {
        // if paid transaction
        List<TransactionProduct> products = transactionProductRepository.findAllByTransaction(data);
        log.info("products count = {}", products.size());
        if (data.getStatus().equals(TransactionStatus.PAID) || data.getStatus().equals(TransactionStatus.PENDING) && !products.isEmpty()) {
            for (TransactionProduct item : products) {
                Product product = item.getProduct();
                product.setStock(product.getIsUnlimited() ? product.getStock() : product.getStock() - item.getQuantity());
                productRepository.save(product);
            }
        }
    }

    private void convertTransactionProduct(TransactionModel.TransactionCreateUpdateRequest dto, Transaction savedData) {
        // Add products to the transaction
        List<TransactionProduct> transactionProducts = new ArrayList<>();
        for (TransactionModel.TransactionItemRequest item : dto.getItems()) {
            // Create a new TransactionProduct
            TransactionProduct transactionProduct = new TransactionProduct();

            // Create and set the ID (TransactionProductId)
            TransactionProductId transactionProductId = new TransactionProductId();
            Product product = TreeGetEntity.parsingProductByProjection(item.getProductId(), productRepository);

            // Set the ID fields
            transactionProductId.setTransactionId(savedData.getId());  // Or savedData.getSecureId() if that's used for ID
            transactionProductId.setProductId(product.getId());

            // Set the ID into the TransactionProduct
            transactionProduct.setId(transactionProductId);
            transactionProduct.setTransaction(savedData);
            transactionProduct.setProduct(product);
            transactionProduct.setQuantity(item.getQuantity());

            transactionProducts.add(transactionProduct);
        }

        // Save all transaction products
        transactionProductRepository.saveAll(transactionProducts);
    }
}
