import { Popover, Position } from '@blueprintjs/core';
import { Menu } from '@blueprintjs/icons';
import { useQueryClient, useSuspenseQuery } from '@tanstack/react-query';
import { Link, useLocation, useParams } from '@tanstack/react-router';
import classNames from 'classnames';
import { useState } from 'react';

import { ProgressTag } from '../../../../../components/ProgressTag/ProgressTag';
import { courseBySlugQueryOptions, courseChaptersQueryOptions } from '../../../../../modules/queries/course';

import './CourseChaptersSidebar.scss';

export default function CourseChaptersSidebar() {
  const { courseSlug } = useParams({ strict: false });
  const location = useLocation();
  const queryClient = useQueryClient();
  const { data: course } = useSuspenseQuery(courseBySlugQueryOptions(courseSlug));
  const {
    data: { data: courseChapters, chaptersMap, chapterProgressesMap },
  } = useSuspenseQuery(courseChaptersQueryOptions(course.jid));

  const [isResponsivePopoverOpen, setIsResponsivePopoverOpen] = useState(false);

  const render = () => {
    return (
      <>
        <div
          className={classNames('course-chapters-sidebar', 'course-chapters-sidebar__full', {
            'course-chapters-sidebar--compact': isInProblemPath(),
            'course-chapters-sidebar--wide': !isInChaptersPath(),
          })}
        >
          {renderChapters({ showName: !isInProblemPath() })}
        </div>

        <div
          className={classNames('course-chapters-sidebar', 'course-chapters-sidebar__responsive', {
            'course-chapters-sidebar--wide': !isInChaptersPath(),
          })}
        >
          <Popover
            content={renderChapters({ showName: true })}
            position={Position.BOTTOM_LEFT}
            isOpen={isResponsivePopoverOpen}
            onInteraction={onResponsivePopoverInteraction}
            usePortal={false}
          >
            <p>
              <Menu />
              &nbsp;<small>Chapters Menu</small>
            </p>
          </Popover>
        </div>
      </>
    );
  };

  const renderChapters = ({ showName }) => {
    const firstUnsolvedChapterIndex = getFirstUnsolvedChapterIndex(courseChapters, chapterProgressesMap);

    return courseChapters.map((courseChapter, idx) => (
      <Link
        key={courseChapter.alias}
        className={classNames('course-chapters-sidebar__item', {
          'course-chapters-sidebar__item--selected': isInChapterPath(courseChapter.alias),
          'course-chapters-sidebar__item--future': idx > firstUnsolvedChapterIndex,
        })}
        to={`/courses/${course.slug}/chapters/${courseChapter.alias}`}
        onClick={() => {
          queryClient.setQueryData(['course-chapter', course.jid, courseChapter.alias], {
            jid: courseChapter.chapterJid,
            name: chaptersMap[courseChapter.chapterJid].name,
          });

          if (isResponsivePopoverOpen) {
            onResponsiveItemClick();
          }
        }}
      >
        <div className="course-chapters-sidebar__item-title">
          {courseChapter.alias}
          {showName && <>. {chaptersMap[courseChapter.chapterJid].name}</>}
          &nbsp;&nbsp;
          {renderProgress(chapterProgressesMap[courseChapter.chapterJid])}
        </div>
      </Link>
    ));
  };

  const isInChaptersPath = () => {
    return location.pathname.includes('/chapters/');
  };

  const isInChapterPath = chapterAlias => {
    const basePath = `/courses/${course.slug}/chapters/${chapterAlias}`;
    return (location.pathname + '/').replace('//', '/').startsWith(basePath);
  };

  const isInProblemPath = () => {
    return location.pathname.includes('/problems/');
  };

  const renderProgress = progress => {
    if (!progress || progress.totalProblems === 0) {
      return null;
    }

    const { solvedProblems, totalProblems } = progress;
    return (
      <ProgressTag num={solvedProblems} denom={totalProblems}>
        {solvedProblems} / {totalProblems}
      </ProgressTag>
    );
  };

  const getFirstUnsolvedChapterIndex = (courseChapters, chapterProgressesMap) => {
    for (let i = courseChapters.length - 1; i >= 0; i--) {
      const progress = chapterProgressesMap[courseChapters[i].chapterJid];
      if (!progress) {
        continue;
      }
      if (progress.totalProblems === 0) {
        continue;
      }
      if (progress.solvedProblems > 0) {
        if (progress.solvedProblems < progress.totalProblems) {
          return i;
        } else {
          for (let j = i + 1; j < courseChapters.length; j++) {
            if (chapterProgressesMap[courseChapters[i].chapterJid]) {
              if (chapterProgressesMap[courseChapters[i].chapterJid].totalProblems > 0) {
                return j;
              }
            }
          }
          return i + 1;
        }
      }
    }
    return 0;
  };

  const onResponsivePopoverInteraction = state => {
    setIsResponsivePopoverOpen(state);
  };

  const onResponsiveItemClick = () => {
    setTimeout(() => {
      setIsResponsivePopoverOpen(false);
    }, 200);
  };

  return render();
}
