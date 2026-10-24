import { Flex } from '@blueprintjs/labs';
import { useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';

import { BreadcrumbTitle } from '../../../../components/BreadcrumbTitle/BreadcrumbTitle';
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
    <ContentCard title={<BreadcrumbTitle parent="Users" current={user.username} />}>
      <Flex flexDirection="column" gap={8}>
        <div>
          <h4>General</h4>
          <FormTable keyStyles={keyStyles} rows={generalRows} />
        </div>
        <UserInfoSection user={user} />
      </Flex>
    </ContentCard>
  );
}
