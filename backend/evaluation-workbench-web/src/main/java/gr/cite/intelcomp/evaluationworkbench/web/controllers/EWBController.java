package gr.cite.intelcomp.evaluationworkbench.web.controllers;

import gr.cite.intelcomp.evaluationworkbench.model.*;
import gr.cite.intelcomp.evaluationworkbench.query.*;
import gr.cite.intelcomp.evaluationworkbench.service.ewb.EWBService;
import gr.cite.intelcomp.evaluationworkbench.web.model.QueryResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/ewb", produces = MediaType.APPLICATION_JSON_VALUE)
public class EWBController {

    private final EWBService service;

    @Autowired
    public EWBController(EWBService service) {
        this.service = service;
    }

    @GetMapping("listAllCollections")
    public QueryResult<String> listAllCollections() {
        List<String> collections = service.getAllCollections();
        return new QueryResult<>(collections, collections.size());
    }

    @GetMapping("listAllCorpus")
    public QueryResult<String> listAllCorpus() {
        List<String> collections = service.getAllCorpus();
        return new QueryResult<>(collections, collections.size());
    }

    @GetMapping("listAllModels")
    public QueryResult<String> listAllModels() {
        List<String> collections = service.getAllModels();
        return new QueryResult<>(collections, collections.size());
    }

    @GetMapping("listModelsByCorpus")
    public QueryResult<String> listModelsByCorpus(@RequestParam("corpus") String corpus) {
        List<String> collections = service.getModelsForCorpora(corpus);
        return new QueryResult<>(collections, collections.size());
    }

    @PostMapping("listDocsWithHighSemanticRelationship")
    public QueryResult<PrettySemanticsModel> listDocsWithHighSemanticRelationship(@RequestBody SemanticsQuery query) {
        List<PrettySemanticsModel> collections = service.querySemanticsRelationships(query);
        return new QueryResult<>(collections, collections.size());
    }

    @PostMapping("getThetasDoc")
    public QueryResult<EWBPrettyTheta> getThetasDoc(@RequestBody ThetasQuery query) {
        List<EWBPrettyTheta> thetas = service.queryThetas(query);
        return new QueryResult<>(thetas);
    }

    @GetMapping("getNrDocColl")
    public Integer getNrDocColl(@RequestParam("collection") String collection) {
        return service.queryNrDocsColl(collection);
    }

    @GetMapping("listCollectionMetadata")
    public QueryResult<String> listCollectionMetadata(@RequestParam("collection") String collection) {
        List<String> metadata = service.queryCollectionMetadata(collection);
        return new QueryResult<>(metadata);
    }

    @PostMapping("queryDocuments")
    public QueryResult<Map<String, Object>> queryDocs(@RequestBody DocsQuery query) {
        DocsQuery countQuery = new DocsQuery();
        countQuery.setLike(query.getLike());
        countQuery.setCorpusCollection(query.getCorpusCollection());
        long count = service.countQueryDocs(countQuery);
        List<Map<String, Object>> docs = service.queryDocs(query);
        return new QueryResult<>(docs, count);
    }

    @GetMapping("getDocument")
    public Map<String, Object> getDocument(@RequestParam("corpus_collection") String collection, @RequestParam("doc_id") String docId) {
        return service.getDocMetadata(collection, docId);
    }

    @GetMapping("topics")
    public QueryResult<EWBTopicModel> getTopics(@RequestParam("model") String model) {
        List<EWBTopicModel> topics = service.getTopics(model);
        return new QueryResult<>(topics);
    }

    @GetMapping("topics/relations")
    public List<TopicRelation> getTopicRelations(@RequestParam String model) {
        EWBCorrelatedTopicsQuery query = new EWBCorrelatedTopicsQuery();
        query.setModelCollection(model);
        return this.service.getCorrelatedTopics(query);
    }

    @GetMapping("topics/temporal")
    public Map<String, Map<Integer, Double>> getTopicsTemporal(@RequestParam String corpus, @RequestParam String model) {
        return this.service.getTemporalTopics(corpus, model);
    }

    @GetMapping("topics/vocabulary")
    public Map<String, List<String>> getTopicVocabulary(@RequestParam String model) {
        return this.service.getTopicsVocabularies(model);
    }

    @PostMapping("similaritiesPairs")
    public Map<String, Double> getSimilarityPair(@RequestBody SemanticsPairQuery semanticsPairQuery) {
        return this.service.getSimilarityPairs(semanticsPairQuery);
    }

    @GetMapping("topics/hierarchical")
    public Map<String, List<EWBTheta>> getHierarchicalTopics(@RequestParam String corpus, @RequestParam String model) {
        return this.service.getHierarchicalTopics(corpus, model);
    }

    @PostMapping("topics/topdocs")
    public List<EWBTopDoc> getTopDocuments(@RequestBody TopicTopDocQuery topicTopDocQuery) {
        return this.service.getTopicTopDocs(topicTopDocQuery);
    }

    @GetMapping("topics/metadata")
    public EWBTopicMetadata getTopicMetadata(@RequestParam String model, @RequestParam String topicId) {
        return this.service.getTopicMetadata(model, topicId);
    }

    @GetMapping("topics/topwords")
    public List<EWBTopicBeta> getTopicTopWords(@RequestParam String model, @RequestParam String topicId) {
        return this.service.getTopicsTopWords(model, topicId);
    }

    @PostMapping("listDocsWithHighSimilarityToText")
    public QueryResult<PrettySemanticsModel> listDocsWithHighSimilarityToText(@RequestBody EWBSimilarTextQuery query) {
        List<PrettySemanticsModel> collections = service.listSimilarDocsByText(query);
        return new QueryResult<>(collections, collections.size());
    }


}
