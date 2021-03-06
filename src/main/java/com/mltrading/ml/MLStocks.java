package com.mltrading.ml;

/**
 * Created by gmo on 26/01/2016.
 */


import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.tree.model.RandomForestModel;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;



/**
 * basic class implementation for machine learning status
 * container of MLStock and MLStatus for result
 */
public class MLStocks  implements Serializable {
    private String codif;
    private Map<PredictionPeriodicity,MLStock> container;

    private MLStatus status;

    JavaRDD<FeaturesStock> testData;


    public MLStocks(String codif) {
        this.codif = codif;
        container = new HashMap<>();
        container.put(PredictionPeriodicity.D1, new MLStock(codif, PredictionPeriodicity.D1));
        container.put(PredictionPeriodicity.D5, new MLStock(codif, PredictionPeriodicity.D5));
        container.put(PredictionPeriodicity.D20, new MLStock(codif, PredictionPeriodicity.D20));
        container.put(PredictionPeriodicity.D40, new MLStock(codif, PredictionPeriodicity.D40));

        status = new MLStatus();
    }

    public MLStock getSock(PredictionPeriodicity period) {
        return container.get(period);
    }


    public MLStatus getStatus() {
        return status;
    }

    public void setStatus(MLStatus status) {
        this.status = status;
    }

    public String getCodif() {
        return codif;
    }

    public void setCodif(String codif) {
        this.codif = codif;
    }


    public void setTestData(JavaRDD<FeaturesStock> testData) {
        this.testData = testData;
    }


    public void load() {
        for (Map.Entry<PredictionPeriodicity, MLStock> entry : container.entrySet()) {
            entry.getValue().load();
        }
    }

    public void loadValidator() {
        for (Map.Entry<PredictionPeriodicity, MLStock> entry : container.entrySet()) {
            entry.getValue().getValidator().loadValidator(codif+"V"+entry.getKey().toString());
        }
    }

    public void saveValidator() {
        for (Map.Entry<PredictionPeriodicity, MLStock> entry : container.entrySet()) {
            entry.getValue().saveValidator();
        }
    }

    /**
     * saveValidator for specific perdiod
     * @param p
     */
    public void saveModel(PredictionPeriodicity p) {
        container.get(p).saveModel();
    }

    public void generateValidator(String methodName, int sector) {

        try {
            Class[] cArg = new Class[1];
            cArg[0] = Integer.class;
            MatrixValidator validator = new MatrixValidator();
            validator.getClass().getMethod(methodName,cArg).invoke(validator, sector);
            for (Map.Entry<PredictionPeriodicity, MLStock> entry : container.entrySet()) {
                entry.getValue().setValidator(validator.clone());
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

    }

    public void setValidators(MatrixValidator validator) {
        for (Map.Entry<PredictionPeriodicity, MLStock> entry : container.entrySet()) {
            entry.getValue().setValidator(validator);
        }
    }



    /**
     * set validator in container
     * @param p
     * @param validator
     */
    public void setValidator(PredictionPeriodicity p, MatrixValidator validator) {
        container.get(p).setValidator(validator);
    }


    public MatrixValidator getValidator(PredictionPeriodicity p) {
        return container.get(p).getValidator();
    }


    public boolean randomizeModel() {
        boolean checkContinue =true;

        for (Map.Entry<PredictionPeriodicity, MLStock> entry : container.entrySet()) {
            checkContinue &= entry.getValue().getValidator().optimizeModel(entry.getValue().getValidator());
        }

        return checkContinue;
    }



    /**
     * set validator in container
     * @param p
     * @param model
     */
    public void setModel(PredictionPeriodicity p, RandomForestModel model) {
        container.get(p).setModel(model);
    }


    public RandomForestModel getModel(PredictionPeriodicity p) {
        return container.get(p).getModel();
    }


    /**
     * recopy mlStock from MLStocks ref for period
     * @param p
     * @param ref
     */
    public void replace(PredictionPeriodicity p,MLStocks ref) {
        this.setModel(p,ref.getModel(p));
        this.setValidator(p, ref.getValidator(p));
    }


    /**
     * recopy mlStock from MLStocks ref for period
     * @param p
     * @param ref
     */
    public void insert(PredictionPeriodicity p,MLStocks ref) {
        this.setModel(p,ref.getModel(p));
        this.getValidator(p).replace(ref.getValidator(p));
    }






    /**
     *
     * @param ref
     * @return
     */
    @Deprecated
    public MLStocks replaceValidator(MLStocks ref) {
        int position = this.getValidator(PredictionPeriodicity.D1).getCol();
        MLStocks copy = new MLStocks(this.getCodif());

        for (Map.Entry<PredictionPeriodicity, MLStock> entry : container.entrySet()) {
            copy.setValidator(entry.getKey(),ref.getValidator(entry.getKey()).clone());
            copy.getValidator(entry.getKey()).setCol(position);
        }

        return copy;
    }


    public void resetScoring() {
        setScoring(false);
    }


    public void setScoring(boolean scoring) {
        for (Map.Entry<PredictionPeriodicity, MLStock> entry : container.entrySet()) {
            entry.getValue().setModelImprove(scoring);
        }
    }


    /**
     *
     * @return
     */
    @Override
    public MLStocks clone() {

        MLStocks copy = new MLStocks(this.getCodif());

        for (Map.Entry<PredictionPeriodicity, MLStock> entry : container.entrySet()) {
            copy.setValidator(entry.getKey(), this.getValidator(entry.getKey()).clone());
        }

        copy.setStatus(this.getStatus().clone());

        return copy;
    }



    public void distibute() {
        for (Map.Entry<PredictionPeriodicity, MLStock> entry : container.entrySet()) {
            entry.getValue().distibute();
        }
    }

    public void send(JavaSparkContext sc) {
        for (Map.Entry<PredictionPeriodicity, MLStock> entry : container.entrySet()) {
            entry.getValue().send(sc);
        }
    }

    public void saveDB() {
        for (Map.Entry<PredictionPeriodicity, MLStock> entry : container.entrySet()) {
            entry.getValue().saveModelDB();
        }
    }


    public void saveDB(PredictionPeriodicity p) {
        container.get(p).saveModelDB();
    }

    public void loadDB() {
        for (Map.Entry<PredictionPeriodicity, MLStock> entry : container.entrySet()) {
            entry.getValue().loadModelDB();
        }
    }

    public void updateColValidator(int col) {
        for (Map.Entry<PredictionPeriodicity, MLStock> entry : container.entrySet()) {
            entry.getValue().getValidator().setCol(col);
        }
    }

    public Map<PredictionPeriodicity,MatrixValidator> getValidators() {
        Map<PredictionPeriodicity,MatrixValidator> mapValidator = new HashMap<>();
        for (Map.Entry<PredictionPeriodicity, MLStock> entry : container.entrySet()) {
            mapValidator.put(entry.getKey(),entry.getValue().getValidator());
        }
        return mapValidator;
    }

    public void mergeValidator(MLStocks ref) {
        for (Map.Entry<PredictionPeriodicity, MLStock> entry : container.entrySet()) {
            entry.getValue().mergetValidator(ref.getValidator(entry.getKey()));
        }
    }
}
