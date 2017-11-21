package com.mltrading.ml;

import org.apache.spark.mllib.tree.model.RandomForestModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MLRank {

    private String codif;
    private RandomForestModel model;
    private boolean modelImprove;
    private static final Logger log = LoggerFactory.getLogger(MLRank.class);

    private MLStatus status;

    public MLRank(String codif) {
        this.codif = codif;
    }

    public String getCodif() {
        return codif;
    }

    public void setCodif(String codif) {
        this.codif = codif;
    }

    public RandomForestModel getModel() {
        return model;
    }

    public void setModel(RandomForestModel model) {
        this.model = model;
    }

    public boolean isModelImprove() {
        return modelImprove;
    }

    public void setModelImprove(boolean modelImprove) {
        this.modelImprove = modelImprove;
    }


    public MLStatus getStatus() {
        return status;
    }

    public void setStatus(MLStatus status) {
        this.status = status;
    }
}
