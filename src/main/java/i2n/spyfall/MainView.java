package i2n.spyfall;

import java.util.Map;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ExternalResource;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import javax.annotation.PostConstruct;

@SpringView(name = MainView.VIEW_NAME)
@SpringComponent(MainView.VIEW_NAME)
@UIScope
public class MainView extends VerticalLayout implements View {

    public final static String VIEW_NAME = "main";

    @Autowired
    private SharedData sharedData;

    @PostConstruct
    protected void init() {
        Button button = new Button("INCREASE COUNTER");
        button.addClickListener(clickEvent -> {

            Map<String, Long> sharedDataMap = sharedData.getSharedDataMap();
            sharedDataMap.putIfAbsent("test", 1L);

            Notification.show(String.valueOf(sharedDataMap.get("test")));
            sharedDataMap.put("test", sharedDataMap.get("test") + 1);
        });
        addComponent(button);
    }
}