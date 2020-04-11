import { mount, ReactWrapper } from 'enzyme';
import * as React from 'react';
import { Provider } from 'react-redux';
import { combineReducers, createStore } from 'redux';
import { reducer as formReducer } from 'redux-form';

import { ProblemType } from '../../../../modules/api/sandalphon/problem';
import { Chapter } from '../../../../modules/api/jerahmeel/chapter';
import { ChapterProblem } from '../../../../modules/api/jerahmeel/chapterProblem';
import { ChapterProblemEditDialog, ChapterProblemEditDialogProps } from './ChapterProblemEditDialog';

const chapter = {
  jid: 'chapter-jid',
} as Chapter;

describe('ChapterProblemEditDialog', () => {
  let onGetProblems: jest.Mock<any>;
  let onSetProblems: jest.Mock<any>;
  let wrapper: ReactWrapper<any, any>;

  const problems: ChapterProblem[] = [
    {
      alias: 'A',
      problemJid: 'jid-1',
      type: ProblemType.Programming,
    },
    {
      alias: 'B',
      problemJid: 'jid-2',
      type: ProblemType.Bundle,
    },
  ];

  const problemsMap = {
    'jid-1': { slug: 'slug-1' },
    'jid-2': { slug: 'slug-2' },
  };

  beforeEach(() => {
    onGetProblems = jest.fn().mockReturnValue(Promise.resolve({ data: problems, problemsMap }));
    onSetProblems = jest.fn().mockReturnValue(() => Promise.resolve({}));

    const store: any = createStore(combineReducers({ form: formReducer }));

    const props: ChapterProblemEditDialogProps = {
      isOpen: true,
      chapter,
      onCloseDialog: jest.fn(),
      onGetProblems,
      onSetProblems,
    };
    wrapper = mount(
      <Provider store={store}>
        <ChapterProblemEditDialog {...props} />
      </Provider>
    );
  });

  test('edit problems dialog form', () => {
    wrapper.update();

    const button = wrapper.find('button[data-key="edit"]');
    button.simulate('click');

    wrapper.update();

    const problemsField = wrapper.find('textarea[name="problems"]');
    expect(problemsField.prop('value')).toEqual('A,slug-1,PROGRAMMING\nB,slug-2,BUNDLE');

    problemsField.simulate('change', { target: { value: 'P, slug-3,PROGRAMMING\n  Q,slug-4,BUNDLE  ' } });

    const form = wrapper.find('form');
    form.simulate('submit');

    expect(onSetProblems).toHaveBeenCalledWith(chapter.jid, [
      {
        alias: 'P',
        slug: 'slug-3',
        type: 'PROGRAMMING',
      },
      {
        alias: 'Q',
        slug: 'slug-4',
        type: 'BUNDLE',
      },
    ]);
  });
});
