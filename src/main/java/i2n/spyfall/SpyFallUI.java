package i2n.spyfall;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.Viewport;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.spring.navigator.SpringNavigator;
import com.vaadin.ui.UI;

import org.springframework.beans.factory.annotation.Autowired;

@SpringUI
@Push
@Theme("valo")
@Title("Spyfall")
@Viewport("width=device-width")
public class SpyFallUI extends UI {

    @Autowired
    private SpringNavigator navigator;

    @Override
    protected void init(VaadinRequest request) {
        navigator.init(this, this);
        setNavigator(navigator);
        navigator.setErrorView(ErrorView.class);
    }

    public static SpyFallUI getCurrent() {
        return (SpyFallUI)UI.getCurrent();
    }

}