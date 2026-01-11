package ec.edu.espe.finvory.controller;

import ec.edu.espe.finvory.model.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

/**
 *
 * @author Arelys Otavalo, the POOwer Rangers of Programming
 */
public class CustomerController {

    private final FinvoryController mainController;

    public CustomerController(FinvoryController mainController) {
        this.mainController = mainController;
    }

    public Customer findCustomerPublic(String format) {
        return findCustomer(format);
    }

    public Customer findCustomer(String id) {
        if (id == null || mainController.data == null) {
            return null;
        }
        for (Customer customer : mainController.data.getCustomers()) {
            if (customer.getIdentification().equals(id)) {
                return customer;
            }
        }
        return null;
    }

    public Customer findById(String id) {
        return findCustomer(id);
    }

    public ArrayList<Customer> findCustomersByQuery(String query) {
        ArrayList<Customer> matches = new ArrayList<>();
        if (query == null || mainController.data == null) {
            return matches;
        }

        String que = query.trim().toLowerCase();
        for (Customer customer : mainController.data.getCustomers()) {
            boolean matchId = customer.getIdentification().startsWith(que);
            boolean matchName = customer.getName().toLowerCase().contains(que);

            if (matchId || matchName) {
                matches.add(customer);
            }
        }
        return matches;
    }

    public Customer handleCreateCustomer(HashMap<String, String> customerData) {
        String id = customerData.get("id");
        if (findCustomer(id) != null) {
            return null;
        }
        Customer newCustomer = new Customer(
                customerData.get("name"),
                id,
                customerData.get("phone"),
                customerData.get("email"),
                customerData.get("clientType")
        );
        mainController.data.addCustomer(newCustomer);
        mainController.saveData();
        return newCustomer;
    }

    public boolean handleAddCustomer(String name, String id, String phone, String email, String type) {
        if (findCustomer(id) != null) {
            return false;
        }

        Customer newCustomer = new Customer(name, id, phone, email, type);
        List<Customer> currentList = mainController.data.getCustomers();

        try {
            currentList.add(newCustomer);
        } catch (UnsupportedOperationException e) {
            List<Customer> mutableList = new ArrayList<>(currentList);
            mutableList.add(newCustomer);
            try {
                currentList.clear();
                currentList.addAll(mutableList);
            } catch (Exception ex) {
                System.err.println("Error crítico: La lista en FinvoryData es inmutable.");
                return false;
            }
        }
        mainController.saveData();
        return true;
    }

    public boolean handleEditCustomerGUI(String customerId, HashMap<String, String> updates) {
        Customer customer = findCustomer(customerId);
        if (customer == null) {
            return false;
        }

        if (!updates.get("name").isEmpty()) {
            customer.setName(updates.get("name"));
        }
        if (!updates.get("phone").isEmpty()) {
            customer.setPhone(updates.get("phone"));
        }
        if (!updates.get("email").isEmpty()) {
            customer.setEmail(updates.get("email"));
        }

        customer.setClientType(updates.get("clientType"));
        mainController.saveData();
        return true;
    }

    public boolean handleUpdateCustomerGUI(String originalId, String name, String phone, String email, String type) {
        Customer customer = findCustomer(originalId);
        if (customer == null) {
            return false;
        }
        try {
            customer.setName(name);
            customer.setPhone(phone);
            customer.setEmail(email);
            customer.setClientType(type);
            mainController.saveData();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean handleDeleteCustomerGUI(String customerId) {
        Customer customer = findCustomer(customerId);
        if (customer == null) {
            return false;
        }

        for (InvoiceSim invoiceSim : mainController.data.getInvoices()) {
            if (invoiceSim.getCustomer().getIdentification().equals(customer.getIdentification())) {
                return false;
            }
        }
        mainController.data.removeCustomer(customer);
        mainController.saveData();
        return true;
    }

    public HashMap<String, Float> getCustomerPurchaseReport() {
        HashMap<String, Float> map = new HashMap<>();
        if (mainController.data == null) {
            return map;
        }

        for (InvoiceSim invoiceSim : mainController.data.getInvoices()) {
            if ("COMPLETED".equals(invoiceSim.getStatus())) {
                map.put(invoiceSim.getCustomer().getName(),
                        map.getOrDefault(invoiceSim.getCustomer().getName(), 0f) + invoiceSim.getTotal().floatValue());
            }
        }
        return map;
    }

    public List<Object[]> getCustomerActivityFlexibleData(int year, int month) {
        List<Object[]> rows = new ArrayList<>();
        Map<String, Integer> frequencyMap = new HashMap<>();
        Map<String, BigDecimal> totalAmountMap = new HashMap<>();

        if (mainController.data == null) {
            return rows;
        }

        for (InvoiceSim invoice : mainController.data.getInvoices()) {
            LocalDate invoiceDate = invoice.getDate();
            boolean yearMatches = (invoiceDate.getYear() == year);
            boolean monthMatches = (month == 0 || invoiceDate.getMonthValue() == month);

            if ("COMPLETED".equals(invoice.getStatus()) && yearMatches && monthMatches) {
                String name = invoice.getCustomer().getName();
                frequencyMap.put(name, frequencyMap.getOrDefault(name, 0) + 1);

                BigDecimal currentTotal = totalAmountMap.getOrDefault(name, BigDecimal.ZERO);
                totalAmountMap.put(name, currentTotal.add(invoice.getTotal()));
            }
        }

        frequencyMap.forEach((name, frequency) -> {
            rows.add(new Object[]{
                name,
                frequency,
                String.format("%.2f", totalAmountMap.get(name))
            });
        });

        rows.sort((rowA, rowB) -> ((Integer) rowB[1]).compareTo((Integer) rowA[1]));
        return rows;
    }

}
