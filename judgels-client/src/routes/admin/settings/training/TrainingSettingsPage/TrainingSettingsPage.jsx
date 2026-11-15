import { Flex } from '@blueprintjs/labs';
import { useQuery } from '@tanstack/react-query';

import { ContentCard } from '../../../../../components/ContentCard/ContentCard';
import { EditableSections } from '../../../../../components/EditableSections/EditableSections';
import { LoadingState } from '../../../../../components/LoadingState/LoadingState';
import { settingsQueryOptions } from '../../../../../modules/queries/setting';
import { SettingsTopbar } from '../../SettingsTopbar/SettingsTopbar';
import { AccountPasswordResetSection } from './AccountPasswordResetSection';
import { AccountRegistrationSection } from './AccountRegistrationSection';
import { GoogleAnalyticsSection } from './GoogleAnalyticsSection';

export default function TrainingSettingsPage() {
  const { data } = useQuery(settingsQueryOptions());

  return (
    <ContentCard title="Settings">
      <Flex flexDirection="column" gap={5}>
        <SettingsTopbar activeItemPath="/admin/settings/training" />
        {data ? (
          <EditableSections>
            <Flex flexDirection="column" gap={8}>
              <AccountRegistrationSection
                trainingAccountRegistration={data.trainingAccountRegistration}
                mailerConfigured={data.mailerConfigured}
                recaptchaConfigured={data.recaptchaConfigured}
              />
              <AccountPasswordResetSection
                trainingAccountPasswordReset={data.trainingAccountPasswordReset}
                mailerConfigured={data.mailerConfigured}
              />
              <GoogleAnalyticsSection trainingGoogleAnalytics={data.trainingGoogleAnalytics} />
            </Flex>
          </EditableSections>
        ) : (
          <LoadingState />
        )}
      </Flex>
    </ContentCard>
  );
}
