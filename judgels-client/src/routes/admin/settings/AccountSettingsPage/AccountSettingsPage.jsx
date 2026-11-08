import { Flex } from '@blueprintjs/labs';
import { useQuery } from '@tanstack/react-query';

import { ContentCard } from '../../../../components/ContentCard/ContentCard';
import { EditableSections } from '../../../../components/EditableSections/EditableSections';
import { LoadingState } from '../../../../components/LoadingState/LoadingState';
import { settingsQueryOptions } from '../../../../modules/queries/setting';
import { SettingsTopbar } from '../SettingsTopbar/SettingsTopbar';
import { UserRegistrationSection } from './UserRegistrationSection';
import { UserResetPasswordSection } from './UserResetPasswordSection';

export default function AccountSettingsPage() {
  const { data } = useQuery(settingsQueryOptions());

  return (
    <ContentCard title="Settings">
      <Flex flexDirection="column" gap={5}>
        <SettingsTopbar activeItemPath="/admin/settings/account" />
        {data ? (
          <EditableSections>
            <Flex flexDirection="column" gap={8}>
              <UserRegistrationSection userRegistration={data.userRegistration} />
              <UserResetPasswordSection userResetPassword={data.userResetPassword} />
            </Flex>
          </EditableSections>
        ) : (
          <LoadingState />
        )}
      </Flex>
    </ContentCard>
  );
}
