import * as React from 'react';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';

import { Card } from 'components/Card/Card';
import { LoadingState } from 'components/LoadingState/LoadingState';
import Pagination from 'components/Pagination/Pagination';
import { AdminsDeleteResponse, AdminsResponse, AdminsUpsertResponse } from 'modules/api/uriel/admin';

import { AdminsTable } from '../AdminsTable/AdminsTable';
import { AdminAddDialog } from '../AdminAddDialog/AdminAddDialog';
import { AdminRemoveDialog } from '../AdminRemoveDialog/AdminRemoveDialog';
import { adminActions as injectedAdminActions } from '../modules/adminActions';

import './AdminsPage.css';

export interface AdminsPageProps {
  onGetAdmins: (page?: number) => Promise<AdminsResponse>;
  onUpsertAdmins: (usernames: string[]) => Promise<AdminsUpsertResponse>;
  onDeleteAdmins: (usernames: string[]) => Promise<AdminsDeleteResponse>;
}

interface AdminsPageState {
  response?: AdminsResponse;
  lastRefreshAdminsTime?: number;
}

class AdminsPage extends React.Component<AdminsPageProps, AdminsPageState> {
  private static PAGE_SIZE = 100;

  state: AdminsPageState = {};

  render() {
    return (
      <Card title="Admins">
        <div className="uriel-admin-page-header" />
        {this.renderAddRemoveDialogs()}
        {this.renderAdmins()}
        {this.renderPagination()}
      </Card>
    );
  }

  private renderAdmins = () => {
    const { response } = this.state;
    if (!response) {
      return <LoadingState />;
    }

    const { data: admins, profilesMap } = response;
    if (admins.totalCount === 0) {
      return (
        <p>
          <small>No admins.</small>
        </p>
      );
    }

    return <AdminsTable admins={admins.page} profilesMap={profilesMap} />;
  };

  private renderPagination = () => {
    // updates pagination when admins are refreshed
    const { lastRefreshAdminsTime } = this.state;
    const key = lastRefreshAdminsTime || 0;

    return <Pagination key={key} currentPage={1} pageSize={AdminsPage.PAGE_SIZE} onChangePage={this.onChangePage} />;
  };

  private onChangePage = async (nextPage: number) => {
    const data = await this.refreshAdmins(nextPage);
    return data.totalCount;
  };

  private refreshAdmins = async (page?: number) => {
    const response = await this.props.onGetAdmins(page);
    this.setState({ response });
    return response.data;
  };

  private renderAddRemoveDialogs = () => {
    const { response } = this.state;
    if (!response) {
      return null;
    }
    return (
      <>
        <AdminAddDialog onUpsertAdmins={this.upsertAdmins} />
        <AdminRemoveDialog onDeleteAdmins={this.deleteAdmins} />
        <div className="clearfix" />
      </>
    );
  };

  private upsertAdmins = async data => {
    const response = await this.props.onUpsertAdmins(data);
    this.setState({ lastRefreshAdminsTime: new Date().getTime() });
    return response;
  };

  private deleteAdmins = async data => {
    const response = await this.props.onDeleteAdmins(data);
    this.setState({ lastRefreshAdminsTime: new Date().getTime() });
    return response;
  };
}

export function createAdminsPage(adminActions) {
  const mapDispatchToProps = {
    onGetAdmins: adminActions.getAdmins,
    onUpsertAdmins: adminActions.upsertAdmins,
    onDeleteAdmins: adminActions.deleteAdmins,
  };

  return withRouter<any>(connect(undefined, mapDispatchToProps)(AdminsPage));
}

export default createAdminsPage(injectedAdminActions);
