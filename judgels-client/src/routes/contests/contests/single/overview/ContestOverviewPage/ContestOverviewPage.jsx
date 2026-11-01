import { Flex } from '@blueprintjs/labs';
import { useQuery, useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';

import { ContentCard } from '../../../../../../components/ContentCard/ContentCard';
import { HtmlText } from '../../../../../../components/HtmlText/HtmlText';
import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import { contestBySlugQueryOptions, contestDescriptionQueryOptions } from '../../../../../../modules/queries/contest';
import ContestRegistrantsButton from '../ContestRegistrantsButton/ContestRegistrantsButton';
import ContestRegistrationCard from '../ContestRegistrationCard/ContestRegistrationCard';

export default function ContestOverviewPage() {
  const { contestSlug } = useParams({ strict: false });
  const { data: contest } = useSuspenseQuery(contestBySlugQueryOptions(contestSlug));
  const { data: response } = useQuery(contestDescriptionQueryOptions(contest.jid));

  const renderRegistration = () => {
    return <ContestRegistrationCard />;
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
    <ContentCard title="Overview" action={<ContestRegistrantsButton />}>
      <Flex flexDirection="column" gap={4}>
        {renderRegistration()}
        {renderDescription()}
      </Flex>
    </ContentCard>
  );
}
