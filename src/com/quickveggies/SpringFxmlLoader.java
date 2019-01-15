package com.quickveggies;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.avalon.framework.activity.Initializable;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import org.springframework.beans.factory.Aware;


public class SpringFxmlLoader
{
    private ApplicationContext context;
 
    public SpringFxmlLoader(ApplicationContext context)
    {
        this.context = context;
    }
    
    

    public Object load(String url, Class<?> controllerClass) throws IOException
    {
        InputStream fxmlStream = null;
        try
        {
            fxmlStream = controllerClass.getResourceAsStream(url);
            Object instance = context.getBean(controllerClass);
            FXMLLoader loader =  new FXMLLoader();
            loader.getNamespace().put("controller", instance);
            return loader.load(fxmlStream);
        }
        catch (Exception e) {
        	e.printStackTrace();
        	throw new RuntimeException(e);
        }
        finally
        {
            if (fxmlStream != null)
            {
                fxmlStream.close();
            }
        }
    }

}

