package gr.cite.intelcomp.evaluationworkbench.model;

import java.util.HashMap;
import java.util.List;

public class ClassificationModel extends HashMap<String, List<String>> {

    public List<ClassificationResponse> getResponseModel() {
        return values().stream().map(val -> {
            ClassificationResponse response = new ClassificationResponse();
            response.setType(val.get(1));
            response.setValue(Float.valueOf(val.get(2)));
            return response;
        }).toList();
    }

}
