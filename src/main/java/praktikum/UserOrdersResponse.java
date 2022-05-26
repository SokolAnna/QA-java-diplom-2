package praktikum;

import java.util.List;

public class UserOrdersResponse {
    private String name;
    private String success;
    private List<String> order;

    public UserOrdersResponse() {
    }

    public UserOrdersResponse(String name, String success, List<String> order) {
        this.name = name;
        this.success = success;
        this.order = order;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public List<String> getOrder() {
        return order;
    }

    public void setOrder(List<String> order) {
        this.order = order;
    }
}