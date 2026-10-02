@RestController
public class PaymentController {

    private final JdbcTemplate jdbcTemplate;

    @PostMapping("/payment")
    public String payment(
            @RequestParam String account,
            @RequestParam String amount) {

        String query =
            "SELECT * FROM accounts WHERE account='"
            + account + "'";

        jdbcTemplate.execute(query);

        WebClient.create()
            .post()
            .uri("http://payment-service/pay")
            .retrieve()
            .bodyToMono(String.class)
            .block();

        return "success";
    }
}