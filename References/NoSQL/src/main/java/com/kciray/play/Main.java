package com.kciray.play;

import com.mongodb.MongoClient;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;

import java.io.IOException;
import java.util.List;

@Configuration
public class Main {

    public static void main(String[] args){
        startServer();

        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Main.class);
        context.getBean(Main.class).run();
    }

    public static void startServer(){
        try {
            MongodStarter starter = MongodStarter.getDefaultInstance();

            IMongodConfig config = new MongodConfigBuilder()
                    .version(Version.Main.PRODUCTION)
                    .net(new Net("localhost", 1234, Network.localhostIsIPv6()))
                    .build();

            starter.prepare(config).start();
        }catch (IOException e){
            System.out.println(e);
        }
    }

    @Bean
    MongoClient mongoClient(){
        return new MongoClient("localhost", 1234);
    }

    @Bean
    MongoTemplate mongoTemplate(MongoClient mongoClient){
        MongoTemplate template = new MongoTemplate(mongoClient, "db");
        return template;
    }

    @Autowired
    MongoTemplate mongoTemplate;

    void run(){
        Product product1 = new Product();
        product1.price = 10.3;
        product1.name = "Mouse";
        mongoTemplate.insert(product1);

        Product product2 = new Product();
        product2.price = 30.3;
        product2.name = "Tablet";
        mongoTemplate.insert(product2);

        System.out.println(mongoTemplate.getCollectionNames());//[product]

        List<Product> products = mongoTemplate.find( new BasicQuery("{price: {$gt:15} }"), Product.class);
        //$gt = greater (>)
        for(Product product: products){
            System.out.println(product.name);
        }
        //Tablet
    }
}
