package ec.espe.edu.finvory.model;

/**
 *
 * @author Arelys Otavalo, The POOwer Rangers Of Programming
 */

public class Product {
    
    private String id;
    private String name;
    private String description;
    private String barcode;
    private float baseCostPrice; 
    private String supplierId;

    public Product() {}

    public Product(String id, String name, String description, String barcode, float baseCostPrice, String supplierId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.barcode = barcode;
        this.baseCostPrice = baseCostPrice;
        this.supplierId = supplierId;
    }
    
        public String getId() { 
            return id; 
        }
        
        public String getName() { 
            return name; 
        }
        
        public String getDescription() { 
            return description; 
        }
        
        public String getBarcode() { 
            return barcode; 
        }
        
        public float getBaseCostPrice() { 
            return baseCostPrice; 
        }
        
        public String getSupplierId() { 
            return supplierId;        
        }
    
        public void setName(String name) { 
            this.name = name; 
        }
        
        public void setDescription(String description) { 
            this.description = description; 
        }
        
        public void setBaseCostPrice(float baseCostPrice) { 
            this.baseCostPrice = baseCostPrice; 
        }
        
        public void setSupplierId(String supplierId) { 
            this.supplierId = supplierId; 
        }
}
    
