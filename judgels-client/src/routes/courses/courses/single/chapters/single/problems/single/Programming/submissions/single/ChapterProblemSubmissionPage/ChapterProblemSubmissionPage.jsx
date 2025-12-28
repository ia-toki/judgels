import { ChevronLeft } from '@blueprintjs/icons';
import { useEffect, useRef, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { useParams, useResolvedPath } from 'react-router-dom';

import { ButtonLink } from '../../../../../../../../../../../../components/ButtonLink/ButtonLink';
import { ContentCard } from '../../../../../../../../../../../../components/ContentCard/ContentCard';
import { LoadingState } from '../../../../../../../../../../../../components/LoadingState/LoadingState';
import { SubmissionDetails } from '../../../../../../../../../../../../components/SubmissionDetails/Programming/SubmissionDetails';
import { selectStatementLanguage } from '../../../../../../../../../../../../modules/webPrefs/webPrefsSelectors';
import { selectCourse } from '../../../../../../../../../modules/courseSelectors';
import { selectCourseChapter } from '../../../../../../../modules/courseChapterSelectors';

import * as breadcrumbsActions from '../../../../../../../../../../../../modules/breadcrumbs/breadcrumbsActions';
import * as chapterProblemSubmissionActions from '../../modules/chapterProblemSubmissionActions';

export default function ChapterProblemSubmissionPage() {
  const { problemAlias, submissionId } = useParams();
  const { pathname } = useResolvedPath('');
  const dispatch = useDispatch();
  const course = useSelector(selectCourse);
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

    return () => {
      dispatch(breadcrumbsActions.popBreadcrumb(pathname));
    };
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
    dispatch(breadcrumbsActions.pushBreadcrumb(pathname, '#' + data.submission.id));

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
