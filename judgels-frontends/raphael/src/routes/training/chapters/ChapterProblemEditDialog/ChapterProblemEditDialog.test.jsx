import { mount } from 'enzyme';
import { Provider } from 'react-redux';
import { combineReducers, createStore } from 'redux';
import { reducer as formReducer } from 'redux-form';

import { ProblemType } from '../../../../modules/api/sandalphon/problem';
import { ChapterProblemEditDialog } from './ChapterProblemEditDialog';

const chapter = {
  jid: 'chapter-jid',
};

describe('ChapterProblemEditDialog', () => {
  let onGetProblems;
  let onSetProblems;
  let wrapper;

  const problems = [
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

    const store = createStore(combineReducers({ form: formReducer }));

    const props = {
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
    expect(problemsField.prop('value')).toEqual('A,slug-1\nB,slug-2,BUNDLE');

    problemsField.getDOMNode().value = 'P, slug-3\n  Q,slug-4,BUNDLE  ';
    problemsField.simulate('input');

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
