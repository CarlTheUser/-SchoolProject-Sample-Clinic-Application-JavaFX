package userinterface.navigation;

import java.net.URL;
import java.util.Map;

public interface UserNavigation{

    boolean navigate(NavigationItem navigationItem);

    void navigateBack();

    void navigateHome();

    class NavigationItem{
        final URL navigationUrl;
        final boolean addToBackStack;
        final Map<String, Object> parameters;

        public NavigationItem(URL navigationUrl, boolean addToBackStack, Map<String, Object> parameters) {
            this.navigationUrl = navigationUrl;
            this.addToBackStack = addToBackStack;
            this.parameters = parameters;
        }

        public NavigationItem(URL navigationUrl, boolean addToBackStack) {
            this.navigationUrl = navigationUrl;
            this.addToBackStack = addToBackStack;
            this.parameters = null;
        }

        public NavigationItem(URL navigationUrl) {
            this.navigationUrl = navigationUrl;
            this.addToBackStack = true;
            this.parameters = null;
        }

        public URL getNavigationUrl() {
            return navigationUrl;
        }

        public boolean isAddToBackStack() {
            return addToBackStack;
        }

        public Map<String, Object> getParameters() {
            return parameters;
        }
    }

    interface NavigationListener{
        void onPreNavigate(PreNavigationArgument argument);

        class PreNavigationArgument{
            URL url;
            boolean cancelNavigate;

            public PreNavigationArgument(URL url) {
                this.url = url;
                cancelNavigate = false;
            }

            public URL getUrl() {
                return url;
            }

            public void setUrl(URL url) {
                this.url = url;
            }

            public boolean isCancelNavigate() {
                return cancelNavigate;
            }

            public void setCancelNavigate(boolean cancelNavigate) {
                this.cancelNavigate = cancelNavigate;
            }
        }
    }

}
