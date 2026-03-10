import { HTMLTable } from '@blueprintjs/core';
import { useQuery, useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';

import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { LoadingState } from '../../../../components/LoadingState/LoadingState';
import { userQueryOptions } from '../../../../modules/queries/user';
import { userInfoQueryOptions } from '../../../../modules/queries/userInfo';

export default function UserViewPage() {
  const { userJid } = useParams({ strict: false });

  const { data: user } = useSuspenseQuery(userQueryOptions(userJid));
  const { data: userInfo } = useQuery(userInfoQueryOptions(userJid));

  const renderUserInfo = () => {
    if (!userInfo) {
      return <LoadingState />;
    }

    return (
      <HTMLTable striped className="table-list">
        <tbody>
          <tr>
            <td>
              <strong>Name</strong>
            </td>
            <td>{userInfo.name || '-'}</td>
          </tr>
          <tr>
            <td>
              <strong>Gender</strong>
            </td>
            <td>{userInfo.gender || '-'}</td>
          </tr>
          <tr>
            <td>
              <strong>Country</strong>
            </td>
            <td>{userInfo.country || '-'}</td>
          </tr>
        </tbody>
      </HTMLTable>
    );
  };

  return (
    <ContentCard>
      <h3>{user.username}</h3>
      <hr />
      <HTMLTable striped className="table-list">
        <tbody>
          <tr>
            <td>
              <strong>JID</strong>
            </td>
            <td>{user.jid}</td>
          </tr>
          <tr>
            <td>
              <strong>Email</strong>
            </td>
            <td>{user.email}</td>
          </tr>
        </tbody>
      </HTMLTable>
      <h4>Info</h4>
      {renderUserInfo()}
    </ContentCard>
  );
}
