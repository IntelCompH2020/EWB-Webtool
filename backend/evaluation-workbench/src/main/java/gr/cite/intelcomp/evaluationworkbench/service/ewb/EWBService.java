package gr.cite.intelcomp.evaluationworkbench.service.ewb;

import gr.cite.intelcomp.evaluationworkbench.model.*;
import gr.cite.intelcomp.evaluationworkbench.query.*;
import gr.cite.intelcomp.evaluationworkbench.webclient.WebClientUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class EWBService {

    private final WebClient ewbTMClient;

    private final WebClient ewbClassificationClient;

    @Autowired
    public EWBService(@Qualifier("ewbTMClient") WebClient ewbTMClient, @Qualifier("ewbClassificationClient") WebClient ewbClassificationClient) {
        this.ewbTMClient = ewbTMClient;
        this.ewbClassificationClient = ewbClassificationClient;
    }

    public List<String> getAllCollections() {
        return ewbTMClient.get().uri("/collections/listCollections").accept(MediaType.APPLICATION_JSON)
                .exchangeToMono(mono -> mono.bodyToMono(new ParameterizedTypeReference<List<String>>() {
                })).block();
    }

    public List<String> getAllCorpus() {
        return ewbTMClient.get().uri("/corpora/listAllCorpus")
                .exchangeToMono(mono -> mono.bodyToMono(new ParameterizedTypeReference<List<String>>() {
                })).block();
    }

    public List<String> getModelsForCorpora(String corpora) {
        return ewbTMClient.get().uri("/corpora/listCorpusModels", builder -> builder.queryParam("corpus_col", corpora).build())
                .exchangeToMono(mono -> mono.bodyToMono(new ParameterizedTypeReference<List<String>>() {
                })).block();
    }

    public List<String> getAllModels() {
        return ewbTMClient.get().uri("/models/listAllModels")
                .exchangeToMono(mono -> mono.bodyToMono(new ParameterizedTypeReference<List<String>>() {
                })).block();
    }

    public List<Map<String, Object>> queryCollection(CollectionQuery query) {
        return ewbTMClient.get().uri("/collections/query", uriBuilder -> WebClientUtils.buildParameters(uriBuilder, query))
                .exchangeToMono(mono -> mono.bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {
                })).block();
    }

    public List<PrettySemanticsModel> querySemanticsRelationships(SemanticsQuery query) {
        List<PrettySemanticsModel> result = new ArrayList<>();
        List<SemanticsModel> semanticsModels = this.querySemanticsRelationshipsInternal(query);
        CollectionQuery collectionQuery = new CollectionQuery();
        collectionQuery.setCollection(query.getCorpusCollection());
        collectionQuery.setQ("id: (" + semanticsModels.stream().map(SemanticsModel::getId).collect(Collectors.joining(" ")) + ")");
        collectionQuery.setQop("OR");
        collectionQuery.setFl("id, title");
        collectionQuery.setRows(query.getRows());
        List<Map<String, Object>> collectedData = this.queryCollection(collectionQuery);
        result = semanticsModels.stream().sorted(Comparator.comparingDouble(SemanticsModel::getScore).reversed()).filter(semanticsModel -> !semanticsModel.getId().equals(query.getDocId())).map(semanticsModel -> {
            PrettySemanticsModel prettySemanticsModel = new PrettySemanticsModel();
            Map<String, Object> doc = collectedData.stream().filter(docl -> docl.get("id").toString().equals(semanticsModel.getId())).findFirst().orElse(new HashMap<>());
            if (!doc.isEmpty()) {
                prettySemanticsModel.setId(doc.get("id").toString());
                prettySemanticsModel.setTitle(doc.get("title").toString());
                prettySemanticsModel.setScore(semanticsModel.getScore());
                return prettySemanticsModel;
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());

        return result;
    }

    private List<SemanticsModel> querySemanticsRelationshipsInternal(SemanticsQuery query) {
        return ewbTMClient.get().uri("/queries/getDocsWithHighSimWithDocByid", uriBuilder -> WebClientUtils.buildParameters(uriBuilder, query))
                .exchangeToMono(mono -> {
                    if (mono.statusCode() != HttpStatus.OK) {
                        return Mono.empty();
                    }
                    return mono.bodyToMono(new ParameterizedTypeReference<List<SemanticsModel>>() {});
                }).block();
    }

    public List<Map<String, Object>> queryLargeThetas(LargeThetasQuery query) {
        return ewbTMClient.get().uri("/queries/getDocsWithThetasLargerThanThr", uriBuilder -> WebClientUtils.buildParameters(uriBuilder, query))
                .exchangeToMono(mono -> mono.bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {
                })).block();
    }

    public Integer queryNrDocsColl(String collection) {
        return Objects.requireNonNull(ewbTMClient.get().uri("/queries/getNrDocsColl", builder -> builder.queryParam("collection", collection).build())
                .exchangeToMono(mono -> mono.bodyToMono(new ParameterizedTypeReference<Map<String, Integer>>() {
                })).block()).entrySet().stream().findFirst().orElse(Map.entry("", -1)).getValue();
    }

    public List<EWBPrettyTheta> queryThetas(ThetasQuery query) {
        String response = Objects.requireNonNull(ewbTMClient.get().uri("/queries/getThetasDocById", uriBuilder -> WebClientUtils.buildParameters(uriBuilder, query))
                .exchangeToMono(mono -> mono.bodyToMono(new ParameterizedTypeReference<Map<String, String>>() {
                })).block()).entrySet().stream().findFirst().orElse(Map.entry("", "")).getValue();
        List<String> thetas = List.of(response.split(" "));
        List<EWBPrettyTheta> thetaList = new ArrayList<>();
        List<EWBTopicModel> topicLabels = this.getTopics(query.getModelName());
        thetas.forEach(s -> {
            String[] values = s.split("\\|");
            if (values.length == 2) {
                String name = topicLabels.stream().filter(ewbTopicModel -> ewbTopicModel.getId().equals(values[0])).findFirst().get().getTpcLabels();
                thetaList.add(new EWBPrettyTheta(values[0], name, Double.parseDouble(values[1])));
            }
        });
        return thetaList;
    }

    public List<String> queryCollectionMetadata(String corpus) {
        return Objects.requireNonNull(ewbTMClient.get().uri("/queries/getCorpusMetadataFields", builder -> builder.queryParam("corpus_collection", corpus).build())
                .exchangeToMono(mono -> mono.bodyToMono(new ParameterizedTypeReference<Map<String, List<String>>>() {
                })).block()).entrySet().stream().findFirst().orElse(Map.entry("", List.of())).getValue();
    }

    public List<String> queryDocsInternal(DocsQuery query) {
        return Objects.requireNonNull(ewbTMClient.get().uri("/queries/getDocsWithString", builder -> WebClientUtils.buildParameters(builder, query))
                .exchangeToMono(mono -> mono.bodyToMono(new ParameterizedTypeReference<List<Map<String, String>>>() {
                })).block()).stream().map(stringStringMap -> stringStringMap.entrySet().stream().findFirst().orElse(Map.entry("", "")).getValue()).collect(Collectors.toList());
    }

    public long countQueryDocs(DocsQuery query) {
        return queryDocsInternal(query).size();
    }

    public List<Map<String, Object>> queryDocs(DocsQuery query) {
        List<String> docIds = queryDocsInternal(query);
        return docIds.stream().map(docId -> {
            Map<String, Object> value = this.getDocMetadata(query.getCorpusCollection(), docId);
            value.forEach((key, value1) -> {
                if (value1 instanceof String) {
                    try {
                        Instant timeValue = Instant.parse((String) value1);
                        value.put(key, timeValue);
                    } catch (DateTimeParseException e) {
                        value.put(key, value1);
                    }
                }
            });
            return value;
        }).collect(Collectors.toList());
    }

    public Map<String, Object> getDocMetadata(String corpus, String docId) {
        return Objects.requireNonNull(ewbTMClient.get().uri("/queries/getMetadataDocById", builder ->
                        builder.queryParam("corpus_collection", corpus).queryParam("doc_id", docId).build())
                .exchangeToMono(mono -> mono.bodyToMono(new ParameterizedTypeReference<List<Map<String, Object>>>() {
                })).block()).stream().peek(doc -> {
                    String simKey = doc.keySet().stream().filter(key -> key.startsWith("sim_")).findFirst().orElse("");
                    doc.remove(simKey);
                    String ndocsKey = doc.keySet().stream().filter(key -> key.equals("nwords_per_doc")).findFirst().orElse("");
                    doc.remove(ndocsKey);
        }).findFirst().orElse(null);
    }

    public List<EWBTopicModel> getTopics(String model) {
        return ewbTMClient.get().uri("/queries/getTopicsLabels", builder -> builder.queryParam("model_collection", model).build())
                .exchangeToMono(mono -> mono.bodyToMono(new ParameterizedTypeReference<List<EWBTopicModel>>() {
                })).block();
    }

    /**Due to the size of the data, keep it static to 10 rows per requests*/
    private static final Integer THETA_MAX_ROWS = 100;

    public Map<String, Map<String, Double>> getThetaRelations(LargeThetasQuery largeThetasQuery) {
        Map<String, Map<String, Double>> relationThetas = new HashMap<>();
        int maxTopics = this.queryNrDocsColl(largeThetasQuery.getModelName());
        largeThetasQuery.setRows(THETA_MAX_ROWS);
        int index = 0;
        List<String> blackListedIds = new ArrayList<>();
        for (int i = 0; i < maxTopics; i++) {
            index = 0;
            largeThetasQuery.setStart(index);
            relationThetas.put("t" + i, new HashMap<>());
            largeThetasQuery.setTopicId(Integer.toString(i));
            List<Map<String, Object>> docs = this.queryLargeThetas(largeThetasQuery);
            docs = docs.stream().filter(doc -> !blackListedIds.contains(doc.get("id").toString())).collect(Collectors.toList());
            do {
                int finalI = i;
                docs.forEach(doc -> {
                    blackListedIds.add(doc.get("id").toString());
                    String topicString = (String) doc.get("doctpc_" + largeThetasQuery.getModelName());
                    List<String> topics = List.of(topicString.split(" "));
                    topics.forEach(topic -> {
                        String[] values = topic.split("\\|");
                        if (!relationThetas.get("t" + finalI).containsKey(values[0])) {
                            relationThetas.get("t" + finalI).put(values[0], Double.parseDouble(values[1]));
                        } else {
                            relationThetas.get("t" + finalI).put(values[0], relationThetas.get("t" + finalI).get(values[0]) + Double.parseDouble(values[1]));
                        }
                    });

                });
                index += THETA_MAX_ROWS;
                largeThetasQuery.setStart(index);
                docs = this.queryLargeThetas(largeThetasQuery);
            } while (docs.size() == THETA_MAX_ROWS);
        }
        return relationThetas;
    }

    private static final Integer MAX_TEMPORAL_STEP = 1000;
    public Map<String, Map<Integer, Double>> getTemporalTopics(String corpus, String model) {
        Map<String, Map<Integer, Double>> result = new TreeMap<>();
        List<Map<String, Object>> docs = new ArrayList<>();
        int index = 0;
        int minYear = 10000;
        int maxYear = 0;
        Integer count = this.queryNrDocsColl(model);
        EWBThetaAndDateQuery query = new EWBThetaAndDateQuery();
        query.setCorpusCollection(corpus);
        query.setModelName(model);
        query.setStart(index);
        query.setRows(MAX_TEMPORAL_STEP);
        List<String> blacklistDocIds = new ArrayList<>();
        String topicField = "doctpc_" + model;
        while(true) {
            query.setStart(index);
            List<Map<String, String>> tdocs = this.getThetasAndDates(query);
            if (tdocs.isEmpty()) {
                break;
            }
            List<Map<String, Object>> parsedDocs = tdocs.stream().map(doc -> {
                String doctpcKey = doc.keySet().stream().filter(key -> key.startsWith("doctpc")).findFirst().orElse("");
                String doctpcValue  = doc.remove(doctpcKey);
                Map<String, Object> newDoc = new HashMap<>();
                newDoc.putAll(doc);
                newDoc.put(doctpcKey, EWBTheta.mapToPojo(doctpcValue));
                return newDoc;
            }).collect(Collectors.toList());
            docs.addAll(parsedDocs);
            index += MAX_TEMPORAL_STEP;
        }
        for (Map<String, Object> doc: docs){
            if (doc.containsKey(topicField)) {
                List<EWBTheta> topics = (List<EWBTheta>) doc.get(topicField);
                if (topics != null) {
                    for (EWBTheta topic : topics) {
                        if (!result.containsKey(topic.getId())) {
                            result.put(topic.getId(), new TreeMap<>());
                        }
                        if (doc.containsKey("date") && doc.get("date") != null) {
                            Integer year = Integer.parseInt(doc.get("date").toString().split("-")[0]);
                            if (year < minYear) {
                                minYear = year;
                            }
                            if (year > maxYear) {
                                maxYear = year;
                            }
                            if (!result.get(topic.getId()).containsKey(year)) {
                                result.get(topic.getId()).put(year, topic.getTheta());
                            } else {
                                result.get(topic.getId()).put(year, result.get(topic.getId()).get(year) + topic.getTheta());
                            }
                        }
                    }
                }
            }
        }
        int finalMinYear = minYear;
        int finalMaxYear = maxYear;
        /*Double maxValue = result.entrySet().stream().map(stringMapEntry -> stringMapEntry.getValue().values()).max(Comparator.comparing(o -> o.stream().max(Double::compareTo).orElse(0.0))).get().stream().max(Double::compareTo).orElse(0.0);
        Double divider = 10.0;
        while(true) {
            if (maxValue / divider <= 0.1) {
                break;
            }
            divider += 10.0;
        }
        Double finalDivider = divider;
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
        decimalFormatSymbols.setDecimalSeparator('.');
        DecimalFormat decimalFormat = new DecimalFormat("0.0000");
        decimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);*/
        result.forEach((key, value) -> {
            for (int i = finalMinYear; i < finalMaxYear + 1; i++) {
                if (!value.containsKey(i)) {
                    value.put(i, 0.0);
                }/* else {
                    value.put(i, Double.parseDouble(decimalFormat.format(value.get(i) / finalDivider)));
                }*/
            }
            result.put(key, value);
        });
        return result;
    }

    private List<Map<String, String>> getThetasAndDates(EWBThetaAndDateQuery query) {
        return this.ewbTMClient.get().uri("/queries/getThetasAndDateAllDocs", builder -> WebClientUtils.buildParameters(builder, query))
                .exchangeToMono(mono -> mono.bodyToMono(new ParameterizedTypeReference<List<Map<String, String>>>() {
                })).block();
    }

    public Map<String, List<EWBTheta>> getHierarchicalTopics(String corpus, String model) {
        Map<String, List<EWBTheta>> result = new HashMap<>();
        LargeThetasQuery largeThetasQuery = new LargeThetasQuery();
        largeThetasQuery.setCorpusCollection(corpus);
        largeThetasQuery.setModelName(model);
        largeThetasQuery.setThreshold(950);
        largeThetasQuery.setRows(10);
        Integer count = this.queryNrDocsColl(model);
        for (int i = 0; i < count; i++) {
            largeThetasQuery.setTopicId(String.valueOf(i));
            List<Map<String, Object>> docs = this.queryLargeThetas(largeThetasQuery);
            final String topicId = "t" + i;
            result.put(topicId, docs.stream().map(doc -> {
               EWBTheta tres = EWBTheta.mapToPojo(doc.get("doctpc_" + model).toString()).stream()
                    .filter(theta -> theta.getId().equals(topicId))
                    .peek(theta -> theta.setId(doc.get("id").toString())).findFirst().orElse(null);
               tres.setTheta(tres.getTheta() * Integer.parseInt(doc.get("nwords_per_doc").toString()));
               return tres;
            })
                            .limit(10)
                    .collect(Collectors.toList()));
        }
        return result;
    }

    public List<EWBTopDoc> getTopicTopDocs(TopicTopDocQuery query) {
        List<EWBTopDoc> results = Objects.requireNonNull(ewbTMClient.get().uri("/queries/getTopicTopDocs/", builder -> WebClientUtils.buildParameters(builder, query))
                .exchangeToMono(mono -> mono.bodyToMono(new ParameterizedTypeReference<List<EWBTopDocResponse>>() {
                })).block()).stream().map(doc -> new EWBTopDoc(doc.getId(), doc.getThetas().get(0).getTheta(), doc.getWords())).collect(Collectors.toList());
        CollectionQuery collectionQuery = new CollectionQuery();
        collectionQuery.setCollection(query.getCorpusCollection());
        collectionQuery.setQ("id: (" + results.stream().map(EWBTopDoc::getId).collect(Collectors.joining(" ")) + ")");
        collectionQuery.setQop("OR");
        collectionQuery.setFl("id, title");
        List<Map<String, Object>> collectedData = this.queryCollection(collectionQuery);
        results.forEach(doc -> doc.setTitle(collectedData.stream().filter(cd -> cd.get("id").equals(doc.getId())).map(cd -> cd.get("title").toString()).findFirst().orElse("N/A")));
        results.sort(Comparator.comparing((EWBTopDoc topDoc) -> topDoc.getTopic() * topDoc.getWords()).reversed());
        return results;
    }

    public Map<String, List<String>> getTopicsVocabularies(String model) {
        Map<String, List<String>> result = new HashMap<>();
        Integer count = this.queryNrDocsColl(model);
        int index = 0;
        final int maxRows = 10;
        EWBTopicModelInfoQuery query = new EWBTopicModelInfoQuery();
        query.setModelName(model);
        query.setRows(maxRows);
        while(index < count) {
            query.setStart(index);
            result.putAll(this.getTopicMetadataInternal(query).stream().map(topicMetadata -> Map.entry(topicMetadata.getId(), topicMetadata.getVocab().stream().map(EWBTopicBeta::getId).collect(Collectors.toList()))).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
            index += maxRows;
        }
        return result;
    }

    public List<EWBTopicBeta> getTopicsTopWords(String model, String topicId) {
        List<EWBTopicBeta> result = new ArrayList<>();
        Integer count = this.queryNrDocsColl(model);
        int index = 0;
        final int maxRows = 100;
        EWBTopicModelInfoQuery query = new EWBTopicModelInfoQuery();
        query.setModelName(model);
        query.setRows(maxRows);
        query.setStart(index);
        result.addAll(this.getTopicMetadataInternal(query).stream().filter(topicMetadata -> topicMetadata.getId().equals(topicId)).map(EWBTopicMetadata::getVocab).flatMap(Collection::stream).sorted(Comparator.comparingInt(EWBTopicBeta::getBeta).reversed()).collect(Collectors.toList()));
        return result;
    }

    public Map<String, Double> getSimilarityPairs(SemanticsPairQuery semanticsPairQuery) {
        Map<String, Double> result = new HashMap<>();
        Integer count = this.queryNrDocsColl(semanticsPairQuery.getModel());
        LargeThetasQuery ltquery = new LargeThetasQuery();
        ltquery.setCorpusCollection(semanticsPairQuery.getCorpus());
        ltquery.setModelName(semanticsPairQuery.getModel());
        ltquery.setThreshold(Integer.valueOf(0));
        for (int i = 0; i < count; i++) {
            ltquery.setTopicId(String.valueOf(i));
            List<Map<String, Object>> docs = this.queryLargeThetas(ltquery);
            docs.forEach(doc -> {
                String docId = doc.get("id").toString();
                List<SemanticsModel> semanticsModelList = SemanticsModel.generateModels(doc.get("sim_" + semanticsPairQuery.getModel()).toString());
                semanticsModelList = semanticsModelList.stream().filter(semanticsModel -> semanticsModel.getScore() >= semanticsPairQuery.getLowerPercent() && semanticsModel.getScore() <= semanticsPairQuery.getHigherPercent()).collect(Collectors.toList());
                for (SemanticsModel semanticsModel : semanticsModelList) {
                    String id = docId + "-" + semanticsModel.getId();
                    String di = semanticsModel.getId() + "-" + docId;
                    if (result.containsKey(id) || result.containsKey(di)) {
                        continue;
                    }
                    result.put(id, semanticsModel.getScore());
                }
            });
        }
        /*DocsQuery docsQuery = new DocsQuery();
        docsQuery.setCorpusCollection(semanticsPairQuery.getCorpus());
        docsQuery.setLike("*");
        List<String> docIds = this.queryDocsInternal(docsQuery);
        for (String docId : docIds) {
            SemanticsQuery semanticsQuery = new SemanticsQuery();
            semanticsQuery.setCorpusCollection(semanticsPairQuery.getCorpus());
            semanticsQuery.setModelName(semanticsPairQuery.getModel());
            semanticsQuery.setDocId(docId);
            List<SemanticsModel> semanticsModelList = this.querySemanticsRelationships(semanticsQuery);
            if (semanticsModelList != null) {
                semanticsModelList = semanticsModelList.stream().filter(semanticsModel -> semanticsModel.getScore() >= semanticsPairQuery.getLowerPercent() && semanticsModel.getScore() <= semanticsPairQuery.getHigherPercent()).collect(Collectors.toList());
                for (SemanticsModel semanticsModel : semanticsModelList) {
                    String id = docId + "-" + semanticsModel.getId();
                    String di = semanticsModel.getId() + "-" + docId;
                    if (result.containsKey(id) || result.containsKey(di)) {
                        continue;
                    }
                    result.put(id, semanticsModel.getScore());
                }
            }
        }*/
        return result.entrySet().stream().skip(semanticsPairQuery.getStart()).limit(semanticsPairQuery.getRows()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private List<EWBTopicMetadata> getTopicMetadataInternal(EWBTopicModelInfoQuery query) {
        return this.ewbTMClient.get().uri("/queries/getModelInfo/", builder -> WebClientUtils.buildParameters(builder, query))
                .exchangeToMono(mono -> mono.bodyToMono(new ParameterizedTypeReference<List<EWBTopicMetadata>>() {
                })).block();
    }

    public EWBTopicMetadata getTopicMetadata(String model, String topicId) {
        EWBTopicMetadata result = null;
        Integer count = this.queryNrDocsColl(model);
        int index = 0;
        final int maxRows = 10;
        EWBTopicModelInfoQuery query = new EWBTopicModelInfoQuery();
        query.setModelName(model);
        query.setRows(maxRows);
        while (index < count) {
            query.setStart(index);
            result = this.getTopicMetadataInternal(query).stream().filter(topicMetadata -> topicMetadata.getId().equals(topicId)).findFirst().orElse(null);
            if (result != null) {
                break;
            }
            index += maxRows;
        }
        return result;
    }

    private static final Integer MAX_TOPICS = 10;

    public List<TopicRelation> getCorrelatedTopics(EWBCorrelatedTopicsQuery query) {
        Integer numOfTopics = this.queryNrDocsColl(query.getModelCollection());
        List<TopicRelation> result = new ArrayList<>();
        List<EWBScore> tempResults;
        query.setRows(MAX_TOPICS);
        for (int i = 0; i < numOfTopics; i++) {
            int index = 0;
            List<EWBScore> totalResults = new ArrayList<>();
            query.setTopicId(i);
            do {
                query.setStart(index);
                tempResults = this.getCorrelatedTopicsInternal(query);
                totalResults.addAll(tempResults);
                index += MAX_TOPICS;
            } while (tempResults.size() == MAX_TOPICS && index < numOfTopics);
            result.add(new TopicRelation("t" + i, totalResults));
        }
        return result;
    }

    private List<EWBScore> getCorrelatedTopicsInternal(EWBCorrelatedTopicsQuery query) {
        return this.ewbTMClient.get().uri("/queries/getMostCorrelatedTopics/", builder -> WebClientUtils.buildParameters(builder, query))
                .exchangeToMono(mono -> mono.bodyToMono(new ParameterizedTypeReference<List<EWBScore>>() {}))
                .block();
    }

    public List<PrettySemanticsModel> listSimilarDocsByText(EWBSimilarTextQuery query) {
        List<PrettySemanticsModel> result;
        List<SemanticsModel> semanticsModels = this.ewbTMClient.get().uri("/queries/getDocsSimilarToFreeText/", builder -> WebClientUtils.buildParameters(builder, query))
                .exchangeToMono(mono -> mono.bodyToMono(new ParameterizedTypeReference<List<SemanticsModel>>() {}))
                .block();

        CollectionQuery collectionQuery = new CollectionQuery();
        collectionQuery.setCollection(query.getCorpus());
        collectionQuery.setQ("id: (" + semanticsModels.stream().map(SemanticsModel::getId).collect(Collectors.joining(" ")) + ")");
        collectionQuery.setQop("OR");
        collectionQuery.setFl("id, title");
        collectionQuery.setRows(query.getRows());
        List<Map<String, Object>> collectedData = this.queryCollection(collectionQuery);
        result = semanticsModels.stream().sorted(Comparator.comparingDouble(SemanticsModel::getScore).reversed()).map(semanticsModel -> {
            PrettySemanticsModel prettySemanticsModel = new PrettySemanticsModel();
            Map<String, Object> doc = collectedData.stream().filter(docl -> docl.get("id").toString().equals(semanticsModel.getId())).findFirst().orElse(new HashMap<>());
            if (!doc.isEmpty()) {
                prettySemanticsModel.setId(doc.get("id").toString());
                prettySemanticsModel.setTitle(doc.get("title").toString());
                prettySemanticsModel.setScore(semanticsModel.getScore());
                return prettySemanticsModel;
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());

        return result;
    }

    public List<EWBSimilarityScore> getPairsOfDocsWithHighSim(EWBSimilarityScoreQuery query) {
        return this.ewbTMClient.get().uri("/queries/getPairsOfDocsWithHighSim/", builder -> WebClientUtils.buildParameters(builder, query))
                .exchangeToMono(mono -> mono.bodyToMono(new ParameterizedTypeReference<List<EWBSimilarityScore>>() {}))
                .block();
    }
}
