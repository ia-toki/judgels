package org.iatoki.judgels.sandalphon.client;

import com.google.common.collect.ImmutableList;
import org.iatoki.judgels.jophiel.activity.BasicActivityKeys;
import org.iatoki.judgels.play.IdentityUtils;
import org.iatoki.judgels.play.InternalLink;
import org.iatoki.judgels.play.LazyHtml;
import org.iatoki.judgels.play.Page;
import org.iatoki.judgels.play.controllers.AbstractJudgelsController;
import org.iatoki.judgels.play.views.html.layouts.headingLayout;
import org.iatoki.judgels.play.views.html.layouts.headingWithActionLayout;
import org.iatoki.judgels.sandalphon.client.html.createClientView;
import org.iatoki.judgels.sandalphon.client.html.editClientView;
import org.iatoki.judgels.sandalphon.client.html.listClientsView;
import org.iatoki.judgels.sandalphon.client.html.viewClientView;
import org.iatoki.judgels.sandalphon.SandalphonControllerUtils;
import org.iatoki.judgels.sandalphon.controllers.securities.Authenticated;
import org.iatoki.judgels.sandalphon.controllers.securities.Authorized;
import org.iatoki.judgels.sandalphon.controllers.securities.HasRole;
import org.iatoki.judgels.sandalphon.controllers.securities.LoggedIn;
import play.data.Form;
import play.db.jpa.Transactional;
import play.filters.csrf.AddCSRFToken;
import play.filters.csrf.RequireCSRFCheck;
import play.i18n.Messages;
import play.mvc.Result;

import javax.inject.Inject;
import javax.inject.Singleton;

@Authenticated(value = {LoggedIn.class, HasRole.class})
@Authorized(value = "admin")
@Singleton
public final class ClientController extends AbstractJudgelsController {

    private static final long PAGE_SIZE = 20;
    private static final String CLIENT = "client";

    private final ClientService clientService;

    @Inject
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @Transactional(readOnly = true)
    public Result index() {
        return listClients(0, "id", "asc", "");
    }

    @Transactional(readOnly = true)
    public Result listClients(long pageIndex, String sortBy, String orderBy, String filterString) {
        Page<Client> pageOfClients = clientService.getPageOfClients(pageIndex, PAGE_SIZE, sortBy, orderBy, filterString);

        LazyHtml content = new LazyHtml(listClientsView.render(pageOfClients, sortBy, orderBy, filterString));
        content.appendLayout(c -> headingWithActionLayout.render(Messages.get("client.list"), new InternalLink(Messages.get("commons.create"), routes.ClientController.createClient()), c));
        SandalphonControllerUtils.getInstance().appendSidebarLayout(content);
        SandalphonControllerUtils.getInstance().appendBreadcrumbsLayout(content, ImmutableList.of(
                new InternalLink(Messages.get("client.clients"), routes.ClientController.index())
        ));

        SandalphonControllerUtils.getInstance().appendTemplateLayout(content, "Clients - List");

        return SandalphonControllerUtils.getInstance().lazyOk(content);
    }

    @Transactional(readOnly = true)
    public Result viewClient(long clientId) throws ClientNotFoundException {
        Client client = clientService.findClientById(clientId);

        LazyHtml content = new LazyHtml(viewClientView.render(client));
        content.appendLayout(c -> headingWithActionLayout.render(Messages.get("client.client") + " #" + client.getId() + ": " + client.getName(), new InternalLink(Messages.get("commons.update"), routes.ClientController.editClient(clientId)), c));
        SandalphonControllerUtils.getInstance().appendSidebarLayout(content);
        SandalphonControllerUtils.getInstance().appendBreadcrumbsLayout(content, ImmutableList.of(
                new InternalLink(Messages.get("client.clients"), routes.ClientController.index()),
                new InternalLink(Messages.get("client.view"), routes.ClientController.viewClient(clientId))
        ));
        SandalphonControllerUtils.getInstance().appendTemplateLayout(content, "Client - View");

        return SandalphonControllerUtils.getInstance().lazyOk(content);
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result createClient() {
        Form<ClientUpsertForm> clientUpsertForm = Form.form(ClientUpsertForm.class);

        return showCreateClient(clientUpsertForm);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postCreateClient() {
        Form<ClientUpsertForm> clientUpsertForm = Form.form(ClientUpsertForm.class).bindFromRequest();

        if (formHasErrors(clientUpsertForm)) {
            return showCreateClient(clientUpsertForm);
        }

        ClientUpsertForm clientUpsertData = clientUpsertForm.get();
        Client client = clientService.createClient(clientUpsertData.name, IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());

        SandalphonControllerUtils.getInstance().addActivityLog(BasicActivityKeys.CREATE.construct(CLIENT, client.getJid(), clientUpsertData.name));

        return redirect(routes.ClientController.index());
    }

    @Transactional(readOnly = true)
    @AddCSRFToken
    public Result editClient(long clientId) throws ClientNotFoundException {
        Client client = clientService.findClientById(clientId);
        ClientUpsertForm clientUpsertData = new ClientUpsertForm();
        clientUpsertData.name = client.getName();
        Form<ClientUpsertForm> clientUpsertForm = Form.form(ClientUpsertForm.class).fill(clientUpsertData);

        return showEditClient(clientUpsertForm, client);
    }

    @Transactional
    @RequireCSRFCheck
    public Result postEditClient(long clientId) throws ClientNotFoundException {
        Client client = clientService.findClientById(clientId);
        Form<ClientUpsertForm> clientUpsertForm = Form.form(ClientUpsertForm.class).bindFromRequest();

        if (formHasErrors(clientUpsertForm)) {
            return showEditClient(clientUpsertForm, client);
        }

        ClientUpsertForm clientUpsertData = clientUpsertForm.get();
        clientService.updateClient(client.getJid(), clientUpsertData.name, IdentityUtils.getUserJid(), IdentityUtils.getIpAddress());

        if (!client.getName().equals(clientUpsertData.name)) {
            SandalphonControllerUtils.getInstance().addActivityLog(BasicActivityKeys.RENAME.construct(CLIENT, client.getJid(), client.getName(), clientUpsertData.name));
        }
        SandalphonControllerUtils.getInstance().addActivityLog(BasicActivityKeys.EDIT.construct(CLIENT, client.getJid(), clientUpsertData.name));

        return redirect(routes.ClientController.index());
    }

    private Result showCreateClient(Form<ClientUpsertForm> clientUpsertForm) {
        LazyHtml content = new LazyHtml(createClientView.render(clientUpsertForm));
        content.appendLayout(c -> headingLayout.render(Messages.get("client.create"), c));
        SandalphonControllerUtils.getInstance().appendSidebarLayout(content);
        SandalphonControllerUtils.getInstance().appendBreadcrumbsLayout(content, ImmutableList.of(
                new InternalLink(Messages.get("client.clients"), routes.ClientController.index()),
                new InternalLink(Messages.get("client.create"), routes.ClientController.createClient())
        ));
        SandalphonControllerUtils.getInstance().appendTemplateLayout(content, "Client - Create");

        return SandalphonControllerUtils.getInstance().lazyOk(content);
    }

    private Result showEditClient(Form<ClientUpsertForm> clientUpsertForm, Client client) {
        LazyHtml content = new LazyHtml(editClientView.render(clientUpsertForm, client.getId()));
        content.appendLayout(c -> headingLayout.render(Messages.get("client.client") + " #" + client.getId() + ": " + client.getName(), c));
        SandalphonControllerUtils.getInstance().appendSidebarLayout(content);
        SandalphonControllerUtils.getInstance().appendBreadcrumbsLayout(content, ImmutableList.of(
                new InternalLink(Messages.get("client.clients"), routes.ClientController.index()),
                new InternalLink(Messages.get("client.update"), routes.ClientController.editClient(client.getId()))
        ));
        SandalphonControllerUtils.getInstance().appendTemplateLayout(content, "Client - Update");

        return SandalphonControllerUtils.getInstance().lazyOk(content);
    }
}
