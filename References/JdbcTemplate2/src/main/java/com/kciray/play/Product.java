package com.kciray.play;


//Domain object "Product"
public class Product {
    String name;
    Double price;

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", price=" + price +
                '}';
    }
}
