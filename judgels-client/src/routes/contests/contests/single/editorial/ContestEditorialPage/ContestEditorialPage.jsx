import { useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';
import { useCallback, useEffect, useState } from 'react';

import { ContentCard } from '../../../../../../components/ContentCard/ContentCard';
import { HtmlText } from '../../../../../../components/HtmlText/HtmlText';
import EditorialLanguageWidget from '../../../../../../components/LanguageWidget/EditorialLanguageWidget';
import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import { ProblemEditorial } from '../../../../../../components/ProblemEditorial/ProblemEditorial';
import { consolidateLanguages } from '../../../../../../modules/api/sandalphon/language';
import { getProblemName } from '../../../../../../modules/api/sandalphon/problem';
import { callAction } from '../../../../../../modules/callAction';
import { contestBySlugQueryOptions } from '../../../../../../modules/queries/contest';
import { useWebPrefs } from '../../../../../../modules/webPrefs';

import * as contestEditorialActions from '../modules/contestEditorialActions';

export default function ContestEditorialPage() {
  const { contestSlug } = useParams({ strict: false });
  const { data: contest } = useSuspenseQuery(contestBySlugQueryOptions(contestSlug));
  const { editorialLanguage } = useWebPrefs();

  const [state, setState] = useState({
    response: undefined,
    defaultLanguage: undefined,
    uniqueLanguages: undefined,
  });

  const loadEditorial = async () => {
    const response = await callAction(contestEditorialActions.getEditorial(contest.jid, editorialLanguage));
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
