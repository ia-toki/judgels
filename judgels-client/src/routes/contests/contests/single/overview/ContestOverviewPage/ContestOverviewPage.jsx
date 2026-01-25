import { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';

import { ContentCard } from '../../../../../../components/ContentCard/ContentCard';
import { HtmlText } from '../../../../../../components/HtmlText/HtmlText';
import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import { selectContest } from '../../../modules/contestSelectors';
import ContestRegistrationCard from '../ContestRegistrationCard/ContestRegistrationCard';

import * as contestActions from '../../../modules/contestActions';

import './ContestOverviewPage.scss';

export default function ContestOverviewPage() {
  const dispatch = useDispatch();
  const contest = useSelector(selectContest);

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
