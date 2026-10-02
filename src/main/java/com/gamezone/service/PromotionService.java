package com.gamezone.service;

import com.gamezone.model.*;

import com.gamezone.persistence.PromotionRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * service for the promos
 */
public class PromotionService {
    private PromotionRepository repository;
    private List<Promotion> inventory;

    /**
     * constructor
     * @param repository for save in promotion repository the new promos
     * @param inventory for create a list of promotions in the class of model
     */
    public PromotionService(PromotionRepository repository,List<Promotion> inventory ){
        this.repository=repository;
        this.inventory=repository.loadAll();
    }

    /**
     * for search the promos already created
     * @param id the identification of the promotions already saved
     * @return can return null or promotion
     */
    public Promotion findPromoByID(String id){
        for (Promotion promotion : inventory){
            if (promotion.getId().equals(id)){
                return promotion;
            }
        }
        return null;
    }

    /**
     * register the new promotion of bulk
     * @param bulkPurchaseDiscount create the promotion of bulk
     */
    public void registerBulkPurchaseDiscount(BulkPurchaseDiscount bulkPurchaseDiscount){
        if(findPromoByID(bulkPurchaseDiscount.getId())!=null){
            throw new IllegalArgumentException("bulk promotion with id:"+ bulkPurchaseDiscount.getId()+" already exist");
        }
        inventory.add(bulkPurchaseDiscount);
        repository.saveAll(inventory);
    }


    /**
     * register a new category discount
     * @param categoryDiscount the category discount
     */
    public void registerCategoryDiscount(CategoryDiscount categoryDiscount){
        if(findPromoByID(categoryDiscount.getId())!=null){
            throw new IllegalArgumentException("category promotion with id:"+ categoryDiscount.getId()+" already exist");
        }
        inventory.add(categoryDiscount);
        repository.saveAll(inventory);
    }


    /**
     * register a new percentage discount
     * @param percentageDiscount the percentage discount
     */
    public void registerPercentageDiscount(PercentageDiscount percentageDiscount){
        if(findPromoByID(percentageDiscount.getId())!=null){
            throw new IllegalArgumentException("percentage promotion with id:"+ percentageDiscount.getId()+" already exist");
        }
        inventory.add(percentageDiscount);
        repository.saveAll(inventory);
    }

    /**
     * serch in the repository for
     * @return inventory, list of all the promotions
     */
    public List<Promotion> listAllPromotions(){
        return inventory;
    }

    /**
     * search in the repository for active promotions
     * @return the actives promotions on repository
     */
    public List<Promotion> listActivePromotions(){
        List<Promotion> activePromotion = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for(Promotion promotion: inventory){
            if(promotion.isActive(today)){
                activePromotion.add(promotion);
            }
        }
        return activePromotion;
    }

    /**
     * search for the best promotion discount in the sale
     * @param sale the sale that are been given
     * @return the best promotion
     */
    public Promotion findBestPromotionFor(Sale sale){
        if(sale==null){
            return null;
        }
        Promotion bestPromotion= null;
        double maxDiscount=0.0;

        for(Promotion promotion: inventory){
            if(promotion.isActive(sale.getSaleDate())){
                double currentDiscount = promotion.calculateDiscount(sale);

                if(currentDiscount>maxDiscount){
                    maxDiscount=currentDiscount;
                    bestPromotion=promotion;
                }
            }
        }

        return bestPromotion;
    }

}
