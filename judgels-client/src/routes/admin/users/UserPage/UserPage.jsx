import { useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';

import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { FormTable } from '../../../../components/forms/FormTable/FormTable';
import { userByUsernameQueryOptions } from '../../../../modules/queries/user';
import { UserInfoSection } from '../UserInfoSection/UserInfoSection';

export default function UserPage() {
  const { username } = useParams({ strict: false });

  const { data: user } = useSuspenseQuery(userByUsernameQueryOptions(username));

  const keyStyles = { width: '250px' };

  const generalRows = [
    { key: 'jid', title: 'JID', value: user.jid },
    { key: 'email', title: 'Email', value: user.email },
  ];

  return (
    <ContentCard title={`Users › ${user.username}`}>
      <div>
        <h4>General</h4>
        <FormTable keyStyles={keyStyles} rows={generalRows} />
      </div>
      <hr />
      <UserInfoSection user={user} />
    </ContentCard>
  );
}
