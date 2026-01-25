import { useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';
import { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';

import { ContentCard } from '../../../../../../components/ContentCard/ContentCard';
import { HtmlText } from '../../../../../../components/HtmlText/HtmlText';
import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import { contestBySlugQueryOptions } from '../../../../../../modules/queries/contest';
import { selectToken } from '../../../../../../modules/session/sessionSelectors';
import ContestRegistrationCard from '../ContestRegistrationCard/ContestRegistrationCard';

import * as contestActions from '../../../modules/contestActions';

import './ContestOverviewPage.scss';

export default function ContestOverviewPage() {
  const { contestSlug } = useParams({ strict: false });
  const token = useSelector(selectToken);
  const { data: contest } = useSuspenseQuery(contestBySlugQueryOptions(token, contestSlug));
  const dispatch = useDispatch();

  const [state, setState] = useState({
    response: undefined,
  });

  const loadDescription = async () => {
    const response = await dispatch(contestActions.getContestDescription(contest.jid));
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
