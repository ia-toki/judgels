import { mount } from 'enzyme';
import { Provider } from 'react-redux';
import { combineReducers, createStore } from 'redux';
import { reducer as formReducer } from 'redux-form';

import { ProblemType } from '../../../../modules/api/sandalphon/problem';
import { ProblemSetProblemEditDialog } from './ProblemSetProblemEditDialog';

const problemSet = {
  jid: 'problemSet-jid',
};

describe('ProblemSetProblemEditDialog', () => {
  let onGetProblems;
  let onSetProblems;
  let wrapper;

  const problems = [
    {
      alias: 'A',
      problemJid: 'jid-1',
      type: ProblemType.Programming,
      contestJids: [],
    },
    {
      alias: 'B',
      problemJid: 'jid-2',
      type: ProblemType.Bundle,
      contestJids: [],
    },
    {
      alias: 'C',
      problemJid: 'jid-3',
      type: ProblemType.Programming,
      contestJids: ['contestJid-1', 'contestJid-2'],
    },
  ];

  const problemsMap = {
    'jid-1': { slug: 'slug-1' },
    'jid-2': { slug: 'slug-2' },
    'jid-3': { slug: 'slug-3' },
  };
  const contestsMap = {
    'contestJid-1': { slug: 'contestSlug-1' },
    'contestJid-2': { slug: 'contestSlug-2' },
  };

  beforeEach(() => {
    onGetProblems = jest.fn().mockReturnValue(Promise.resolve({ data: problems, problemsMap, contestsMap }));
    onSetProblems = jest.fn().mockReturnValue(() => Promise.resolve({}));

    const store = createStore(combineReducers({ form: formReducer }));

    const props = {
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
    expect(problemsField.prop('value')).toEqual(
      'A,slug-1\nB,slug-2,BUNDLE\nC,slug-3,PROGRAMMING,contestSlug-1;contestSlug-2'
    );

    problemsField.simulate('change', { target: { value: 'P, slug-3\n  Q,slug-4,BUNDLE  ,contestSlug-3 ' } });

    const form = wrapper.find('form');
    form.simulate('submit');

    expect(onSetProblems).toHaveBeenCalledWith(problemSet.jid, [
      {
        alias: 'P',
        slug: 'slug-3',
        type: 'PROGRAMMING',
        contestSlugs: [],
      },
      {
        alias: 'Q',
        slug: 'slug-4',
        type: 'BUNDLE',
        contestSlugs: ['contestSlug-3'],
      },
    ]);
  });
});
