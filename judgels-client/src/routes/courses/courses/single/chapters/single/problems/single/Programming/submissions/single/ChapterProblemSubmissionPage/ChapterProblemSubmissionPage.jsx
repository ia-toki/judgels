import { ChevronLeft } from '@blueprintjs/icons';
import { useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';
import { useEffect, useRef, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';

import { ButtonLink } from '../../../../../../../../../../../../components/ButtonLink/ButtonLink';
import { ContentCard } from '../../../../../../../../../../../../components/ContentCard/ContentCard';
import { LoadingState } from '../../../../../../../../../../../../components/LoadingState/LoadingState';
import { SubmissionDetails } from '../../../../../../../../../../../../components/SubmissionDetails/Programming/SubmissionDetails';
import { courseBySlugQueryOptions } from '../../../../../../../../../../../../modules/queries/course';
import { selectToken } from '../../../../../../../../../../../../modules/session/sessionSelectors';
import { selectStatementLanguage } from '../../../../../../../../../../../../modules/webPrefs/webPrefsSelectors';
import { createDocumentTitle } from '../../../../../../../../../../../../utils/title';
import { selectCourseChapter } from '../../../../../../../modules/courseChapterSelectors';

import * as chapterProblemSubmissionActions from '../../modules/chapterProblemSubmissionActions';

export default function ChapterProblemSubmissionPage() {
  const { courseSlug, problemAlias, submissionId } = useParams({ strict: false });
  const dispatch = useDispatch();
  const token = useSelector(selectToken);
  const { data: course } = useSuspenseQuery(courseBySlugQueryOptions(token, courseSlug));
  const chapter = useSelector(selectCourseChapter);
  const statementLanguage = useSelector(selectStatementLanguage);

  const [state, setState] = useState({
    submissionWithSource: undefined,
    sourceImageUrl: undefined,
    profile: undefined,
    problemName: undefined,
    containerName: undefined,
  });

  useEffect(() => {
    refreshSubmission();
  }, []);

  const render = () => {
    return (
      <ContentCard>
        <h3 className="heading-with-button-action">Submission #{submissionId}</h3>
        <ButtonLink
          small
          icon={<ChevronLeft />}
          to={`/courses/${course.slug}/chapters/${chapter.alias}/problems/${problemAlias}/submissions`}
        >
          Back
        </ButtonLink>
        <hr />

        {renderSubmission()}
      </ContentCard>
    );
  };

  const refreshSubmission = async () => {
    const { data, profile, problemName, containerName } = await dispatch(
      chapterProblemSubmissionActions.getSubmissionWithSource(+submissionId, statementLanguage)
    );
    document.title = createDocumentTitle(`Submission #${data.submission.id}`);

    setState({
      submissionWithSource: data,
      profile,
      problemName,
      containerName,
    });
  };

  const renderSubmission = () => {
    const { submissionWithSource, profile, sourceImageUrl } = state;

    if (!submissionWithSource) {
      return <LoadingState />;
    }

    return (
      <SubmissionDetails
        submission={submissionWithSource.submission}
        source={submissionWithSource.source}
        profile={profile}
        problemUrl={`/courses/${course.slug}/chapters/${chapter.alias}/problems/${problemAlias}`}
        hideSource={!!!submissionWithSource.source}
        hideSourceFilename
        showLoaderWhenPending
      />
    );
  };

  return render();
}
