import { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';

import { ContentCard } from '../../../../../../components/ContentCard/ContentCard';
import { HtmlText } from '../../../../../../components/HtmlText/HtmlText';
import EditorialLanguageWidget from '../../../../../../components/LanguageWidget/EditorialLanguageWidget';
import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import { ProblemEditorial } from '../../../../../../components/ProblemEditorial/ProblemEditorial';
import { consolidateLanguages } from '../../../../../../modules/api/sandalphon/language';
import { getProblemName } from '../../../../../../modules/api/sandalphon/problem';
import { selectEditorialLanguage } from '../../../../../../modules/webPrefs/webPrefsSelectors';
import { selectContest } from '../../../modules/contestSelectors';

import * as contestEditorialActions from '../modules/contestEditorialActions';

export default function ContestEditorialPage() {
  const dispatch = useDispatch();
  const contest = useSelector(selectContest);
  const editorialLanguage = useSelector(selectEditorialLanguage);

  const [state, setState] = useState({
    response: undefined,
    defaultLanguage: undefined,
    uniqueLanguages: undefined,
  });

  const loadEditorial = async () => {
    const response = await dispatch(contestEditorialActions.getEditorial(contest.jid, editorialLanguage));
    const { defaultLanguage, uniqueLanguages } = consolidateLanguages(response.problemEditorialsMap, editorialLanguage);

    setState({
      response,
      defaultLanguage,
      uniqueLanguages,
    });
  };

  useEffect(() => {
    loadEditorial();
  }, [editorialLanguage]);

  const render = () => {
    return (
      <ContentCard>
        <h3>Editorial</h3>
        <hr />
        {renderEditorialLanguageWidget()}
        {renderEditorial()}
      </ContentCard>
    );
  };

  const renderEditorialLanguageWidget = () => {
    const { defaultLanguage, uniqueLanguages } = state;
    if (!defaultLanguage || !uniqueLanguages) {
      return null;
    }

    const props = {
      defaultLanguage,
      editorialLanguages: uniqueLanguages,
    };
    return <EditorialLanguageWidget {...props} />;
  };

  const renderEditorial = () => {
    const { response } = state;
    if (!response) {
      return <LoadingState />;
    }

    const { preface, problems, problemsMap, problemEditorialsMap, problemMetadatasMap, profilesMap } = response;

    return (
      <div className="contest-editorial">
        {renderPreface(preface, profilesMap)}
        {problems
          .filter(p => problemMetadatasMap[p.problemJid].hasEditorial)
          .map(p =>
            renderProblemEditorial(
              p,
              problemsMap[p.problemJid],
              problemEditorialsMap[p.problemJid],
              problemMetadatasMap[p.problemJid],
              profilesMap
            )
          )}
      </div>
    );
  };

  const renderPreface = (preface, profilesMap) => {
    if (!preface) {
      return null;
    }
    return (
      <ContentCard>
        <HtmlText profilesMap={profilesMap}>{preface}</HtmlText>
      </ContentCard>
    );
  };

  const renderProblemEditorial = (problem, problemInfo, editorial, metadata, profilesMap) => {
    return (
      <ContentCard key={problem.problemJid + state.defaultLanguage}>
        <ProblemEditorial
          title={`${problem.alias}. ${getProblemName(problemInfo, state.defaultLanguage)}`}
          settersMap={metadata.settersMap}
          profilesMap={profilesMap}
        >
          {editorial.text}
        </ProblemEditorial>
      </ContentCard>
    );
  };

  return render();
}
