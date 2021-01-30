package org.iatoki.judgels.sandalphon.problem.bundle.item.multiplechoice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.Set;
import judgels.sandalphon.api.problem.bundle.MultipleChoiceItemConfig;
import org.iatoki.judgels.sandalphon.problem.bundle.item.ItemConfigAdapter;
import org.iatoki.judgels.sandalphon.problem.bundle.item.multiplechoice.html.multipleChoiceItemConfigView;
import play.api.mvc.Call;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Http;
import play.twirl.api.Html;

public final class MultipleChoiceItemConfigAdapter implements ItemConfigAdapter {
    private final ObjectMapper mapper;

    public MultipleChoiceItemConfigAdapter(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Form<?> generateForm(FormFactory formFactory) {
        MultipleChoiceItemConfigForm form = new MultipleChoiceItemConfigForm();
        form.choiceAliases = Lists.newArrayList("a", "b", "c", "d", "e");
        form.choiceContents = Lists.newArrayList("", "", "", "", "");
        form.isCorrects = Lists.newArrayList(null, null, null, null, null);
        return formFactory.form(MultipleChoiceItemConfigForm.class).fill(form);
    }

    @Override
    public Form<?> generateForm(FormFactory formFactory, String conf, String meta) {
        MultipleChoiceItemConfig itemConf = parseConfString(conf);
        MultipleChoiceItemConfigForm itemForm = new MultipleChoiceItemConfigForm();
        itemForm.statement = itemConf.getStatement();
        itemForm.meta = meta;
        itemForm.score = itemConf.getScore();
        itemForm.penalty = itemConf.getPenalty();
        itemForm.choiceAliases = Lists.newArrayList();
        itemForm.choiceContents = Lists.newArrayList();
        itemForm.isCorrects = Lists.newArrayList();
        for (MultipleChoiceItemConfig.Choice itemChoice : itemConf.getChoices()) {
            itemForm.choiceAliases.add(itemChoice.getAlias());
            itemForm.choiceContents.add(itemChoice.getContent().replace("'", "\\'"));
            if (itemChoice.getIsCorrect().orElse(false)) {
                itemForm.isCorrects.add(true);
            } else {
                itemForm.isCorrects.add(null);
            }
        }

        return formFactory.form(MultipleChoiceItemConfigForm.class).fill(itemForm);
    }

    @Override
    public Html getConfHtml(Form<?> form, Call target, String submitLabel) {
        return multipleChoiceItemConfigView.render((Form<MultipleChoiceItemConfigForm>) form, target, submitLabel);
    }

    @Override
    public Form bindFormFromRequest(FormFactory formFactory, Http.Request req) {
        Form<MultipleChoiceItemConfigForm> form = formFactory.form(MultipleChoiceItemConfigForm.class).bindFromRequest(req);
        if (!(form.hasErrors() || form.hasGlobalErrors())) {
            MultipleChoiceItemConfigForm confForm = form.get();
            Set<String> uniqueChoiceAliases = Sets.newHashSet(confForm.choiceAliases);
            if (uniqueChoiceAliases.size() != confForm.choiceAliases.size()) {
                form = form.withGlobalError("Duplicate choice aliases.");
            }
        }
        return form;
    }

    @Override
    public String getMetaFromForm(Form form) {
        MultipleChoiceItemConfigForm itemForm = ((Form<MultipleChoiceItemConfigForm>) form).get();

        return itemForm.meta;
    }

    @Override
    public String processRequestForm(Form form) {
        MultipleChoiceItemConfigForm itemForm = ((Form<MultipleChoiceItemConfigForm>) form).get();

        ImmutableList.Builder<MultipleChoiceItemConfig.Choice> itemChoiceBuilder = ImmutableList.builder();
        for (int i = 0; i < itemForm.choiceContents.size(); ++i) {
            boolean isCorrect = false;
            if ((itemForm.isCorrects != null) && (itemForm.isCorrects.size() > i) && (itemForm.isCorrects.get(i) != null)) {
                isCorrect = true;
            }
            itemChoiceBuilder.add(new MultipleChoiceItemConfig.Choice.Builder()
                    .alias(itemForm.choiceAliases.get(i))
                    .content(itemForm.choiceContents.get(i))
                    .isCorrect(isCorrect)
                    .build());
        }

        MultipleChoiceItemConfig itemConf = new MultipleChoiceItemConfig.Builder()
                .statement(itemForm.statement)
                .score(itemForm.score)
                .penalty(itemForm.penalty)
                .choices(itemChoiceBuilder.build())
                .build();

        try {
            return mapper.writeValueAsString(itemConf);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public MultipleChoiceItemConfig parseConfString(String conf) {
        try {
            return mapper.readValue(conf, MultipleChoiceItemConfig.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
