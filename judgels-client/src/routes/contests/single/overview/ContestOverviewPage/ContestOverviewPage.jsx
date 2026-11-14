import { Callout } from '@blueprintjs/core';
import { BanCircle } from '@blueprintjs/icons';
import { Flex } from '@blueprintjs/labs';
import { useQuery, useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';

import { ContentCard } from '../../../../../components/ContentCard/ContentCard';
import { HtmlText } from '../../../../../components/HtmlText/HtmlText';
import { LoadingState } from '../../../../../components/LoadingState/LoadingState';
import { contestBySlugQueryOptions, contestDescriptionQueryOptions } from '../../../../../modules/queries/contest';
import { useSession } from '../../../../../modules/session';
import ContestRegistrantsButton from '../ContestRegistrantsButton/ContestRegistrantsButton';

import ContestRegistrationActions from '../ContestRegistrationActions/ContestRegistrationActions';

export default function ContestOverviewPage() {
  const { contestSlug } = useParams({ strict: false });
  const { isLoggedIn } = useSession();
  const { data: contest } = useSuspenseQuery(contestBySlugQueryOptions(contestSlug));
  const { data: response } = useQuery(contestDescriptionQueryOptions(contest.jid));

  const renderLoginToRegister = () => {
    if (isLoggedIn) {
      return null;
    }
    return (
      <Callout icon={<BanCircle />} className="secondary-info">
        Please log in to register.
      </Callout>
    );
  };

  const renderDescription = () => {
    if (response === undefined) {
      return <LoadingState />;
    }

    const { description, profilesMap } = response;
    if (!description) {
      return null;
    }

    return <HtmlText profilesMap={profilesMap}>{description}</HtmlText>;
  };

  return (
    <ContentCard
      title="Overview"
      action={
        <Flex gap={1} alignItems="center">
          <ContestRegistrationActions />
          <ContestRegistrantsButton />
        </Flex>
      }
    >
      <Flex flexDirection="column" gap={4}>
        {renderLoginToRegister()}
        {renderDescription()}
      </Flex>
    </ContentCard>
  );
}
