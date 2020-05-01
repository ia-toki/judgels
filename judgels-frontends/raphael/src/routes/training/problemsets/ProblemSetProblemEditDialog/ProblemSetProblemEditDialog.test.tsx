import { mount, ReactWrapper } from 'enzyme';
import * as React from 'react';
import { Provider } from 'react-redux';
import { combineReducers, createStore } from 'redux';
import { reducer as formReducer } from 'redux-form';

import { ProblemType } from '../../../../modules/api/sandalphon/problem';
import { ProblemSet } from '../../../../modules/api/jerahmeel/problemSet';
import { ProblemSetProblem } from '../../../../modules/api/jerahmeel/problemSetProblem';
import { ProblemSetProblemEditDialog, ProblemSetProblemEditDialogProps } from './ProblemSetProblemEditDialog';

const problemSet = {
  jid: 'problemSet-jid',
} as ProblemSet;

describe('ProblemSetProblemEditDialog', () => {
  let onGetProblems: jest.Mock<any>;
  let onSetProblems: jest.Mock<any>;
  let wrapper: ReactWrapper<any, any>;

  const problems: ProblemSetProblem[] = [
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

    const props: ProblemSetProblemEditDialogProps = {
      isOpen: true,
      problemSet,
      onCloseDialog: jest.fn(),
      onGetProblems,
      onSetProblems,
    };
    wrapper = mount(
      <Provider store={store}>
        <ProblemSetProblemEditDialog {...props} />
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

    expect(onSetProblems).toHaveBeenCalledWith(problemSet.jid, [
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
