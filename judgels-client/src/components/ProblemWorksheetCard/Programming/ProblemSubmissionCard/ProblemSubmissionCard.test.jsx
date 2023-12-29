import { mount } from 'enzyme';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router';
import createMockStore from 'redux-mock-store';

import { ProblemSubmissionCard } from './ProblemSubmissionCard';

describe('ProblemSubmissionCard', () => {
  let gradingEngine;
  let gradingLanguageRestriction;
  let onSubmit;
  let reasonNotAllowedToSubmit;
  let submissionWarning;
  let preferredGradingLanguage;

  let wrapper;

  const render = () => {
    const props = {
      config: {
        sourceKeys: {
          encoder: 'Encoder',
          decoder: 'Decoder',
        },
        gradingEngine,
        gradingLanguageRestriction,
      },
      onSubmit,
      reasonNotAllowedToSubmit,
      submissionWarning,
      preferredGradingLanguage,
    };

    const store = createMockStore()({});

    wrapper = mount(
      <Provider store={store}>
        <MemoryRouter>
          <ProblemSubmissionCard {...props} />
        </MemoryRouter>
      </Provider>
    );
  };

  beforeEach(() => {
    gradingEngine = 'Batch';
    preferredGradingLanguage = 'Cpp11';
    gradingLanguageRestriction = { allowedLanguageNames: [] };
    onSubmit = jest.fn();
    reasonNotAllowedToSubmit = undefined;
    submissionWarning = 'Submission Warning';
  });

  describe('when it is not allowed to submit', () => {
    beforeEach(() => {
      reasonNotAllowedToSubmit = 'Contest is over';
      render();
    });

    it('does not show the form and shows the reason', () => {
      expect(wrapper.find('form')).toHaveLength(0);
      expect(wrapper.find('[data-key="reason-not-allowed-to-submit"]').text()).toEqual('Contest is over');
    });
  });

  describe('when it is allowed to submit', () => {
    describe('when there is no grading language restriction', () => {
      beforeEach(() => {
        render();
      });

      it('shows the preferred grading language as default value in the dropdown', () => {
        expect(wrapper.find('button[data-key="gradingLanguage"]').text()).toContain('C++11');
      });
    });

    describe('when there is grading language restriction', () => {
      beforeEach(() => {
        gradingLanguageRestriction = { allowedLanguageNames: ['Pascal', 'Python3'] };
        render();
      });

      it('shows the first grading language as default value in the dropdown', () => {
        expect(wrapper.find('button[data-key="gradingLanguage"]').text()).toContain('Pascal');
      });
    });
  });

  it('shows the correct form inputs', () => {
    render();
    expect(wrapper.find('input[name="sourceFiles.encoder"]')).toHaveLength(1);
    expect(wrapper.find('input[name="sourceFiles.decoder"]')).toHaveLength(1);
    expect(wrapper.find('[name="gradingLanguage"]').length).toBeTruthy();
  });

  it('shows the submission warning', () => {
    render();
    expect(wrapper.find('[data-key="submission-warning"]').at(1).text()).toContain('Submission Warning');
  });

  describe('when the grading engine is OutputOnly', () => {
    beforeEach(() => {
      gradingEngine = 'OutputOnly';
      render();
    });

    it('does not show the grading language input', () => {
      expect(wrapper.find('[name="gradingLanguage"]')).toHaveLength(0);
    });
  });

  describe('when the grading engine is OutputOnlyWithSubtasks', () => {
    beforeEach(() => {
      gradingEngine = 'OutputOnlyWithSubtasks';
      render();
    });

    it('does not show the grading language input', () => {
      expect(wrapper.find('[name="gradingLanguage"]')).toHaveLength(0);
    });
  });
});
