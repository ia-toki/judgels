import { Intent } from '@blueprintjs/core';
import { SendMessage } from '@blueprintjs/icons';
import { useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';

import { ButtonLink } from '../../../../../components/ButtonLink/ButtonLink';
import { HtmlText } from '../../../../../components/HtmlText/HtmlText';
import { courseBySlugQueryOptions, courseChaptersQueryOptions } from '../../../../../modules/queries/course';
import { useSession } from '../../../../../modules/session';

import './CourseOverview.scss';

export default function CourseOverview() {
  const { courseSlug } = useParams({ strict: false });
  const { token } = useSession();
  const { data: course } = useSuspenseQuery(courseBySlugQueryOptions(token, courseSlug));
  const {
    data: { data: chapters },
  } = useSuspenseQuery(courseChaptersQueryOptions(token, course.jid));

  const renderStartButton = () => {
    if (!chapters || chapters.length === 0) {
      return null;
    }
    return (
      <div>
        <ButtonLink intent={Intent.WARNING} to={`/courses/${course.slug}/chapters/${chapters[0].alias}`}>
          Start first chapter&nbsp;&nbsp;
          <SendMessage />
        </ButtonLink>
      </div>
    );
  };

  return (
    <div className="course-overview">
      <h2>{course.name}</h2>
      <HtmlText>{course.description}</HtmlText>
      <br />
      {renderStartButton()}
    </div>
  );
}
