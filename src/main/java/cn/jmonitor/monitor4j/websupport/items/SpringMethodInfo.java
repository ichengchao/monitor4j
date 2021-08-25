package cn.jmonitor.monitor4j.websupport.items;

public class SpringMethodInfo {

    private final String className;
    private final String method;

    public SpringMethodInfo(String className, String method) {
        super();
        this.className = className;
        this.method = method;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((className == null) ? 0 : className.hashCode());
        result = prime * result + ((method == null) ? 0 : method.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SpringMethodInfo other = (SpringMethodInfo) obj;
        if (className == null) {
            if (other.className != null)
                return false;
        } else if (!className.equals(other.className))
            return false;
        if (method == null) {
            if (other.method != null)
                return false;
        } else if (!method.equals(other.method))
            return false;
        return true;
    }

    public String getClassName() {
        return className;
    }

    public String getMethod() {
        return method;
    }

}
