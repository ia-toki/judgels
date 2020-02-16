import * as React from 'react';
import { RouteComponentProps, withRouter } from 'react-router';
import { connect } from 'react-redux';
import { push } from 'connected-react-router';
import { parse, stringify } from 'query-string';
import { HTMLTable, Icon } from '@blueprintjs/core';

import { withBreadcrumb } from '../../../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import { Card } from '../../../../components/Card/Card';
import { LoadingState } from '../../../../components/LoadingState/LoadingState';
import Pagination from '../../../../components/Pagination/Pagination';
import { UserRef } from '../../../../components/UserRef/UserRef';
import { Page, OrderDir } from '../../../../modules/api/pagination';
import { User } from '../../../../modules/api/jophiel/user';
import * as userActions from '../../modules/userActions';

import './UsersPage.css';

export interface UsersPageProps extends RouteComponentProps<{ page: string; orderDir: OrderDir; orderBy: string }> {
  onGetUsers: (page?: number, orderBy?: string, orderDir?: OrderDir) => Promise<Page<User>>;
  onAppendRoute: (queries: any) => any;
}

interface UsersPageState {
  users?: Page<User>;
  page?: number;
  orderBy?: string;
  orderDir?: OrderDir;
}

export class UsersPage extends React.PureComponent<UsersPageProps, UsersPageState> {
  private static PAGE_SIZE = 250;
  private static HEADER = ['Username'];
  private static DEFAULT_ORDER_BY = 'username';
  private static DEFAULT_ORDER_DIR = OrderDir.ASC;

  state: UsersPageState = { page: 1 };

  async componentDidMount() {
    const queries = parse(this.props.location.search);
    let orderDir = UsersPage.DEFAULT_ORDER_DIR;
    if (queries.orderDir in OrderDir) {
      orderDir = queries.orderDir;
    }
    let orderBy = UsersPage.DEFAULT_ORDER_BY;
    if (UsersPage.HEADER.filter(header => header.toLowerCase() === queries.orderBy).length > 0) {
      orderBy = queries.orderBy;
    }
    await this.setState({ orderDir, orderBy });
    this.props.onAppendRoute({ ...queries, orderBy, orderDir });
    this.updateUsers();
  }

  render() {
    const { users } = this.state;
    if (!users) {
      return <LoadingState />;
    }
    return (
      <Card title="Users">
        <HTMLTable striped className="table-list users-page">
          {this.renderHeader()}
          {this.renderRows(users.page)}
        </HTMLTable>
        <Pagination currentPage={1} pageSize={UsersPage.PAGE_SIZE} onChangePage={this.onChangePage} />
      </Card>
    );
  }

  private renderHeader = () => {
    return (
      <thead>
        <tr>
          {UsersPage.HEADER.map((header, index) => (
            <th key={index} onClick={this.setOrder(header.toLowerCase())}>
              {this.withCaret(header)}
            </th>
          ))}
        </tr>
      </thead>
    );
  };

  private renderRows = (data: User[]) => {
    return (
      <tbody>
        {data.map(user => (
          <tr>
            <td>
              <UserRef profile={user} />
            </td>
          </tr>
        ))}
      </tbody>
    );
  };

  private withCaret = (title: string) => {
    const { orderBy, orderDir } = this.state;
    if (orderBy === title.toLowerCase()) {
      return (
        <span>
          {title} <Icon icon={orderDir === OrderDir.DESC ? 'caret-up' : 'caret-down'} />
        </span>
      );
    }
    return title;
  };

  private onChangePage = async (nextPage: number) => {
    await this.setState({ page: nextPage });
    const users = await this.updateUsers();
    return users.totalCount;
  };

  private setOrder = (orderBy: string) => {
    return async () => {
      let orderDir = OrderDir.ASC;
      if (this.state.orderBy === orderBy && this.state.orderDir !== OrderDir.DESC) {
        orderDir = OrderDir.DESC;
      }
      await this.setState({ orderBy, orderDir });
      const queries = parse(this.props.location.search);
      this.props.onAppendRoute({ ...queries, orderBy, orderDir });
      this.updateUsers();
    };
  };

  private updateUsers = async () => {
    const { page, orderBy, orderDir } = this.state;
    const users = await this.props.onGetUsers(page, orderBy, orderDir);
    this.setState({ users });
    return users;
  };
}

const mapDispatchToProps = {
  onGetUsers: userActions.getUsers,
  onAppendRoute: (queries: any) => {
    return push({ search: stringify(queries) });
  },
};

export default withBreadcrumb('Users')(withRouter<any, any>(connect(undefined, mapDispatchToProps)(UsersPage)));
