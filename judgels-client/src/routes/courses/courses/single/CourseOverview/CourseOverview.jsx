import { Intent } from '@blueprintjs/core';
import { SendMessage } from '@blueprintjs/icons';
import { Flex } from '@blueprintjs/labs';
import { useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from '@tanstack/react-router';

import { ButtonLink } from '../../../../../components/ButtonLink/ButtonLink';
import { ContentCard } from '../../../../../components/ContentCard/ContentCard';
import { HtmlText } from '../../../../../components/HtmlText/HtmlText';
import { courseBySlugQueryOptions } from '../../../../../modules/queries/course';
import { courseChaptersQueryOptions } from '../../../../../modules/queries/courseChapter';
import { splitCourseDescription } from '../../splitCourseDescription';

import './CourseOverview.scss';

export default function CourseOverview() {
  const { courseSlug } = useParams({ strict: false });
  const { data: course } = useSuspenseQuery(courseBySlugQueryOptions(courseSlug));
  const {
    data: { data: chapters },
  } = useSuspenseQuery(courseChaptersQueryOptions(course.jid));
  const { imageUrl, text } = splitCourseDescription(course.description);

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
    <Flex className="course-content course-overview" flexDirection="column" flex={1}>
      <h2>{course.name}</h2>
      <ContentCard>
        <Flex flexDirection="column" gap={4}>
          <div className="course-overview__description">
            {imageUrl && <img className="course-overview__image" src={imageUrl} alt="" />}
            <HtmlText>{text}</HtmlText>
          </div>
          {renderStartButton()}
        </Flex>
      </ContentCard>
    </Flex>
  );
}
