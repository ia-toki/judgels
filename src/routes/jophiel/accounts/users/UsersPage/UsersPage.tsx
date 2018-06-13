import * as React from 'react';
import { withRouter } from 'react-router';
import { connect } from 'react-redux';

import { withBreadcrumb } from '../../../../../components/BreadcrumbWrapper/BreadcrumbWrapper';
import { Card } from '../../../../../components/Card/Card';
import { userActions as injectedUserActions } from '../modules/userActions';
import { Page } from '../../../../../modules/api/pagination';
import { User } from '../../../../../modules/api/jophiel/user';
import { LoadingState } from '../../../../../components/LoadingState/LoadingState';
import Pagination from '../../../../../components/Pagination/Pagination';
import { UserRef } from '../../../../../components/UserRef/UserRef';

export interface UsersPageProps {
  onFetchList: (page: number) => Promise<Page<User>>;
}

interface UsersPageState {
  users?: Page<User>;
}

export class UsersPage extends React.PureComponent<UsersPageProps, UsersPageState> {
  private static PAGE_SIZE = 20;
  state: UsersPageState = {};

  async componentDidMount() {
    const users = await this.props.onFetchList(1);
    this.setState({ users });
  }

  render() {
    const { users } = this.state;
    if (!users) {
      return <LoadingState />;
    }
    return (
      <Card title="Users">
        <table className="pt-html-table pt-html-table-striped table-list">
          <thead>
            <tr>
              <th>Username</th>
            </tr>
          </thead>
          <tbody>
            {users.data.map(user => (
              <tr>
                <td>
                  <UserRef user={user} />
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        <Pagination currentPage={1} pageSize={UsersPage.PAGE_SIZE} onChangePage={this.onChangePage} />
      </Card>
    );
  }

  private onChangePage = async (nextPage: number) => {
    const users = await this.props.onFetchList(nextPage);
    this.setState({ users });
    return users.totalData;
  };
}

function createUsersPage(userActions) {
  const mapDispatchToProps = {
    onFetchList: userActions.fetchList,
  };

  return withRouter<any>(connect(undefined, mapDispatchToProps)(UsersPage));
}

export default withBreadcrumb('Users')(createUsersPage(injectedUserActions));
