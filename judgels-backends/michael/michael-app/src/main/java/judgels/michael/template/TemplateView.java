package judgels.michael.template;

import io.dropwizard.views.View;
import java.time.Duration;
import java.time.Instant;

public abstract class TemplateView extends View {
    private final HtmlTemplate template;
    private HtmlForm form;

    public TemplateView(String templateName, HtmlTemplate template) {
        super(templateName);
        this.template = template;
    }

    public TemplateView(String templateName, HtmlTemplate template, HtmlForm form) {
        this(templateName, template);
        this.form = form;
    }

    public HtmlTemplate getVars() {
        return template;
    }

    public HtmlForm getForm() {
        return form;
    }

    public String getDurationFromNow(Instant instant) {
        long duration = Duration.between(instant, Instant.now()).getSeconds();

        long oneSecond = 1;
        long oneMinute = 60 * oneSecond;
        long oneHour = 60 * oneMinute;
        long oneDay = 24 * oneHour;
        long oneMonth = 30 * oneDay;
        long oneYear = 365 * oneDay;

        if (duration >= 0 && duration < 10 * oneSecond) {
            return "just now";
        }

        int sign = duration > 0 ? 1 : -1;
        duration = Math.abs(duration);

        String res;
        if (duration >= oneYear) {
            long years = Math.round((double) duration / oneYear);
            res = years + " year" + (years > 1 ? "s" : "");
        } else if (duration >= oneMonth) {
            long months = Math.round((double) duration / oneMonth);
            res = months + " month" + (months > 1 ? "s" : "");
        } else if (duration >= oneDay) {
            long days = Math.round((double) duration / oneDay);
            res = days + " day" + (days > 1 ? "s" : "");
        } else if (duration >= oneHour) {
            long hours = duration / oneHour;
            res = hours + " hour" + (hours > 1 ? "s" : "");
        } else if (duration >= oneMinute) {
            long minutes = duration / oneMinute;
            res = minutes + " min" + (minutes > 1 ? "s" : "");
        } else {
            res = duration + " sec" + (duration > 1 ? "s" : "");
        }

        if (sign > 0) {
            return res + " ago";
        } else {
            return "in " + res;
        }
    }
}
