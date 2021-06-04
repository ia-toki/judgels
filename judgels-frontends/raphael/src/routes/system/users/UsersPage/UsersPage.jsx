import { Component } from 'react';
import { withRouter } from 'react-router';
import { connect } from 'react-redux';
import { push } from 'connected-react-router';
import { parse, stringify } from 'query-string';
import { HTMLTable, Icon } from '@blueprintjs/core';

import { withBreadcrumb } from '../../../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import { FormattedRelative } from '../../../../components/FormattedRelative/FormattedRelative';
import { Card } from '../../../../components/Card/Card';
import { LoadingState } from '../../../../components/LoadingState/LoadingState';
import Pagination from '../../../../components/Pagination/Pagination';
import { UserRef } from '../../../../components/UserRef/UserRef';
import { OrderDir } from '../../../../modules/api/pagination';
import * as userActions from '../../modules/userActions';

import './UsersPage.scss';

export class UsersPage extends Component {
  static PAGE_SIZE = 250;
  static HEADER = ['Username'];
  static DEFAULT_ORDER_BY = 'username';
  static DEFAULT_ORDER_DIR = OrderDir.ASC;

  state = {
    users: undefined,
    lastSessionTimesMap: undefined,
    orderBy: undefined,
    orderDir: undefined,
    page: 1,
  };

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

  renderHeader = () => {
    return (
      <thead>
        <tr>
          {UsersPage.HEADER.map((header, index) => (
            <th key={index} onClick={this.setOrder(header.toLowerCase())}>
              {this.withCaret(header)}
            </th>
          ))}
          <th>Last login</th>
        </tr>
      </thead>
    );
  };

  renderRows = data => {
    const { lastSessionTimesMap } = this.state;
    return (
      <tbody>
        {data.map(user => (
          <tr>
            <td>
              <UserRef profile={user} />
            </td>
            <td>
              <FormattedRelative value={lastSessionTimesMap[user.jid]} />
            </td>
          </tr>
        ))}
      </tbody>
    );
  };

  withCaret = title => {
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

  onChangePage = async nextPage => {
    await this.setState({ page: nextPage });
    const users = await this.updateUsers();
    return users.totalCount;
  };

  setOrder = orderBy => {
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

  updateUsers = async () => {
    const { page, orderBy, orderDir } = this.state;
    const { data: users, lastSessionTimesMap } = await this.props.onGetUsers(page, orderBy, orderDir);
    this.setState({ users, lastSessionTimesMap });
    return users;
  };
}

const mapDispatchToProps = {
  onGetUsers: userActions.getUsers,
  onAppendRoute: queries => {
    return push({ search: stringify(queries) });
  },
};

export default withBreadcrumb('Users')(withRouter(connect(undefined, mapDispatchToProps)(UsersPage)));
