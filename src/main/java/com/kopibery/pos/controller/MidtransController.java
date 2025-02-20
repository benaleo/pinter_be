// package com.kopibery.pos.controller;

// import com.midtrans.Midtrans;
// import com.midtrans.httpclient.CoreApi;
// import com.midtrans.httpclient.SnapApi;
// import com.midtrans.httpclient.error.MidtransError;

// import io.swagger.v3.oas.annotations.security.SecurityRequirement;
// import io.swagger.v3.oas.annotations.tags.Tag;
// import lombok.AllArgsConstructor;
// import lombok.extern.slf4j.Slf4j;

// import org.json.JSONObject;
// import org.springframework.web.bind.annotation.*;
// import org.springframework.http.ResponseEntity;

// import java.util.HashMap;
// import java.util.Map;
// import java.util.UUID;

// /**
//  * MidtransController
//  */
// @RestController
// @AllArgsConstructor
// @RequestMapping(MidtransController.urlRoute)
// @Tag(name = "Payment API")
// @Slf4j
// @SecurityRequirement(name = "Authorization")
// public class MidtransController {

//     static final String urlRoute = "/api/v1/payment";

//     @PostMapping("/create-transaction")
//     public ResponseEntity<String> createTransaction() throws MidtransError {
//         String transactionToken = createTransaction();
//         return ResponseEntity.ok(transactionToken);
//     }

//     @GetMapping("/transaction-status/{transactionId}")
//     public ResponseEntity<JSONObject> checkTransactionStatus(@PathVariable(name = "transactionId") String transactionId) throws MidtransError {
//         JSONObject transactionStatus = checkTransactionStatusMethod(transactionId);
//         return ResponseEntity.ok(transactionStatus);
//     }

//     public static String createTransactionMethod() throws MidtransError {
//         Map<String, Object> params = requestBody();
//         return SnapApi.createTransactionToken(params);
//     }

//     public static JSONObject checkTransactionStatusMethod(String transactionId) throws MidtransError {
//         return coreApi.checkTransaction(orderId);;
//     }

//     public static Map<String, Object> requestBody() {
//         UUID idRand = UUID.randomUUID();
//         Map<String, Object> params = new HashMap<>();

//         Map<String, String> transactionDetails = new HashMap<>();
//         transactionDetails.put("order_id", idRand.toString());
//         transactionDetails.put("gross_amount", "265000");

//         Map<String, String> creditCard = new HashMap<>();
//         creditCard.put("secure", "true");

//         params.put("transaction_details", transactionDetails);
//         params.put("credit_card", creditCard);

//         return params;
//     }

//     public static void main(String[] args) throws MidtransError {
//         Midtrans.serverKey = "YOUR_SERVER_KEY";
//         Midtrans.isProduction = false;

//         // Create transaction
//         String transactionToken = createTransactionMethod();
//         System.out.println("Transaction Token: " + transactionToken);

//         // Check transaction status
//         JSONObject transactionStatus = checkTransactionStatusMethod(transactionToken);
//         System.out.println("Transaction Status: " + transactionStatus);
//     }
// }
