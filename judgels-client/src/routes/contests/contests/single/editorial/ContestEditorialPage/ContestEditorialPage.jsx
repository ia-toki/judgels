import { useQuery, useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';
import { useMemo } from 'react';

import { ContentCard } from '../../../../../../components/ContentCard/ContentCard';
import { HtmlText } from '../../../../../../components/HtmlText/HtmlText';
import EditorialLanguageWidget from '../../../../../../components/LanguageWidget/EditorialLanguageWidget';
import { LoadingState } from '../../../../../../components/LoadingState/LoadingState';
import { ProblemEditorial } from '../../../../../../components/ProblemEditorial/ProblemEditorial';
import { consolidateLanguages } from '../../../../../../modules/api/sandalphon/language';
import { getProblemName } from '../../../../../../modules/api/sandalphon/problem';
import { contestBySlugQueryOptions } from '../../../../../../modules/queries/contest';
import { contestEditorialQueryOptions } from '../../../../../../modules/queries/contestEditorial';
import { useWebPrefs } from '../../../../../../modules/webPrefs';

export default function ContestEditorialPage() {
  const { contestSlug } = useParams({ strict: false });
  const { data: contest } = useSuspenseQuery(contestBySlugQueryOptions(contestSlug));
  const { editorialLanguage } = useWebPrefs();

  const { data: response } = useQuery(contestEditorialQueryOptions(contest.jid, { language: editorialLanguage }));

  const { defaultLanguage, uniqueLanguages } = useMemo(() => {
    if (!response) {
      return {};
    }
    return consolidateLanguages(response.problemEditorialsMap, editorialLanguage);
  }, [response, editorialLanguage]);

  const renderEditorialLanguageWidget = () => {
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
      <ContentCard key={problem.problemJid + defaultLanguage}>
        <ProblemEditorial
          title={`${problem.alias}. ${getProblemName(problemInfo, defaultLanguage)}`}
          settersMap={metadata.settersMap}
          profilesMap={profilesMap}
        >
          {editorial.text}
        </ProblemEditorial>
      </ContentCard>
    );
  };

  return (
    <ContentCard>
      <h3>Editorial</h3>
      <hr />
      {renderEditorialLanguageWidget()}
      {renderEditorial()}
    </ContentCard>
  );
}
