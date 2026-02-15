import { useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';
import { useEffect, useState } from 'react';

import { ContentCard } from '../../../../../../components/ContentCard/ContentCard';
import { HtmlText } from '../../../../../../components/HtmlText/HtmlText';
import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import { callAction } from '../../../../../../modules/callAction';
import { contestBySlugQueryOptions } from '../../../../../../modules/queries/contest';
import { useSession } from '../../../../../../modules/session';
import ContestRegistrationCard from '../ContestRegistrationCard/ContestRegistrationCard';

import * as contestActions from '../../../modules/contestActions';

import './ContestOverviewPage.scss';

export default function ContestOverviewPage() {
  const { contestSlug } = useParams({ strict: false });
  const { token } = useSession();
  const { data: contest } = useSuspenseQuery(contestBySlugQueryOptions(token, contestSlug));

  const [state, setState] = useState({
    response: undefined,
  });

  const loadDescription = async () => {
    const response = await callAction(contestActions.getContestDescription(contest.jid));
    setState({
      response,
    });
  };

  useEffect(() => {
    loadDescription();
  }, []);

  const render = () => {
    return (
      <>
        {renderRegistration()}
        {renderDescription()}
      </>
    );
  };

  const renderRegistration = () => {
    return <ContestRegistrationCard />;
  };

  const renderDescription = () => {
    const { response } = state;

    if (response === undefined) {
      return <LoadingState />;
    }

    const { description, profilesMap } = response;
    if (!description) {
      return null;
    }

    return (
      <ContentCard>
        <HtmlText profilesMap={profilesMap}>{description}</HtmlText>
      </ContentCard>
    );
  };

  return render();
}
