import { ChevronLeft } from '@blueprintjs/icons';
import { useQuery, useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';
import { useEffect } from 'react';

import { ButtonLink } from '../../../../../../../../../../../../components/ButtonLink/ButtonLink';
import { ContentCard } from '../../../../../../../../../../../../components/ContentCard/ContentCard';
import { LoadingState } from '../../../../../../../../../../../../components/LoadingState/LoadingState';
import { SubmissionDetails } from '../../../../../../../../../../../../components/SubmissionDetails/Programming/SubmissionDetails';
import { courseBySlugQueryOptions } from '../../../../../../../../../../../../modules/queries/course';
import { submissionWithSourceQueryOptions } from '../../../../../../../../../../../../modules/queries/submissionProgramming';
import { useWebPrefs } from '../../../../../../../../../../../../modules/webPrefs';
import { createDocumentTitle } from '../../../../../../../../../../../../utils/title';

export default function ChapterProblemSubmissionPage() {
  const { courseSlug, chapterAlias, problemAlias, submissionId } = useParams({ strict: false });
  const { data: course } = useSuspenseQuery(courseBySlugQueryOptions(courseSlug));
  const { statementLanguage } = useWebPrefs();

  const { data: response } = useQuery(submissionWithSourceQueryOptions(+submissionId, { language: statementLanguage }));

  useEffect(() => {
    if (response) {
      document.title = createDocumentTitle(`Submission #${response.data.submission.id}`);
    }
  }, [response]);

  const renderSubmission = () => {
    if (!response) {
      return <LoadingState />;
    }

    const { data: submissionWithSource, profile } = response;

    return (
      <SubmissionDetails
        submission={submissionWithSource.submission}
        source={submissionWithSource.source}
        profile={profile}
        problemUrl={`/courses/${course.slug}/chapters/${chapterAlias}/problems/${problemAlias}`}
        hideSource={!!!submissionWithSource.source}
        hideSourceFilename
        showLoaderWhenPending
      />
    );
  };

  return (
    <ContentCard>
      <h3 className="heading-with-button-action">Submission #{submissionId}</h3>
      <ButtonLink
        small
        icon={<ChevronLeft />}
        to={`/courses/${course.slug}/chapters/${chapterAlias}/problems/${problemAlias}/submissions`}
      >
        Back
      </ButtonLink>
      <hr />

      {renderSubmission()}
    </ContentCard>
  );
}
