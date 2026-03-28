import { Button, HTMLTable, Intent } from '@blueprintjs/core';
import { Edit } from '@blueprintjs/icons';
import { Flex } from '@blueprintjs/labs';
import { useMutation, useSuspenseQuery } from '@tanstack/react-query';
import { useState } from 'react';

import { Alias } from '../../../../components/forms/validations';
import { courseChaptersQueryOptions, setCourseChaptersMutationOptions } from '../../../../modules/queries/course';
import CourseChaptersEditForm from '../CourseChaptersEditForm/CourseChaptersEditForm';

import * as toastActions from '../../../../modules/toast/toastActions';

export function CourseChaptersSection({ course }) {
  const { data: chaptersResponse } = useSuspenseQuery(courseChaptersQueryOptions(course.jid));
  const setChaptersMutation = useMutation(setCourseChaptersMutationOptions(course.jid));

  const [isEditing, setIsEditingChapters] = useState(false);

  const updateChapters = data => {
    const chapters = deserializeChapters(data.chapters);
    setChaptersMutation.mutate(chapters, {
      onSuccess: () => toastActions.showSuccessToast('Course chapters updated.'),
    });
    setIsEditingChapters(false);
  };

  const renderEditButton = () => {
    return (
      !isEditing && (
        <Button small intent={Intent.PRIMARY} icon={<Edit />} onClick={() => setIsEditingChapters(true)}>
          Edit
        </Button>
      )
    );
  };

  const renderContent = () => {
    if (isEditing) {
      const initialValues = { chapters: serializeChapters(chaptersResponse.data) };
      return (
        <CourseChaptersEditForm
          initialValues={initialValues}
          validator={validateChapters}
          onSubmit={updateChapters}
          onCancel={() => setIsEditingChapters(false)}
        />
      );
    }
    const { data, chaptersMap } = chaptersResponse;
    const rows = data.map(courseChapter => (
      <tr key={courseChapter.chapterJid}>
        <td>{courseChapter.alias}</td>
        <td>{chaptersMap[courseChapter.chapterJid] && chaptersMap[courseChapter.chapterJid].name}</td>
      </tr>
    ));
    return (
      <HTMLTable striped className="table-list-condensed">
        <thead>
          <tr>
            <th style={{ width: '50px' }}>Alias</th>
            <th>Name</th>
          </tr>
        </thead>
        <tbody>{rows}</tbody>
      </HTMLTable>
    );
  };

  return (
    <div>
      <Flex asChild justifyContent="space-between" alignItems="baseline">
        <h4>
          <span>Chapters</span>
          {renderEditButton()}
        </h4>
      </Flex>
      {renderContent()}
    </div>
  );
}

function serializeChapters(chapters) {
  return chapters.map(c => `${c.alias},${c.chapterJid}`).join('\n');
}

function deserializeChapters(chapters) {
  return chapters
    .split('\n')
    .map(s => s.trim())
    .filter(s => s.length > 0)
    .map(s => s.split(','))
    .map(s => s.map(t => t.trim()))
    .map(s => ({
      alias: s[0],
      chapterJid: s[1],
    }));
}

function validateChapters(value) {
  const chapters = value
    .split('\n')
    .map(s => s.trim())
    .filter(s => s.length > 0)
    .map(s => s.split(','))
    .map(s => s.map(t => t.trim()));

  const aliases = [];
  const chapterJids = [];

  for (const c of chapters) {
    if (c.length !== 2) {
      return 'Each line must contain 2 comma-separated elements';
    }
    const alias = c[0];
    const aliasValidation = Alias(alias);
    if (aliasValidation) {
      return 'Chapter aliases: ' + aliasValidation;
    }

    const chapterJid = c[1];

    aliases.push(alias);
    chapterJids.push(chapterJid);
  }

  if (new Set(aliases).size !== aliases.length) {
    return 'Chapter aliases must be unique';
  }
  if (new Set(chapterJids).size !== chapterJids.length) {
    return 'Chapter JIDs must be unique';
  }

  return undefined;
}
