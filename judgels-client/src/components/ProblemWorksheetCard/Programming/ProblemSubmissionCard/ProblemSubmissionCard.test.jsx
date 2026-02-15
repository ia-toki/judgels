import { act, render, screen } from '@testing-library/react';
import { vi } from 'vitest';

import { TestRouter } from '../../../../test/RouterWrapper';
import { ProblemSubmissionCard } from './ProblemSubmissionCard';

describe('ProblemSubmissionCard', () => {
  let gradingEngine;
  let gradingLanguageRestriction;
  let onSubmit;
  let reasonNotAllowedToSubmit;
  let submissionWarning;
  let preferredGradingLanguage;

  const renderComponent = async () => {
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

    return await act(async () =>
      render(
        <TestRouter>
          <ProblemSubmissionCard {...props} />
        </TestRouter>
      )
    );
  };

  beforeEach(() => {
    gradingEngine = 'Batch';
    preferredGradingLanguage = 'Cpp11';
    gradingLanguageRestriction = { allowedLanguageNames: [] };
    onSubmit = vi.fn();
    reasonNotAllowedToSubmit = undefined;
    submissionWarning = 'Submission Warning';
  });

  describe('when it is not allowed to submit', () => {
    beforeEach(() => {
      reasonNotAllowedToSubmit = 'Contest is over';
    });

    it('does not show the form and shows the reason', async () => {
      const { container } = await renderComponent();
      expect(screen.queryByRole('form')).not.toBeInTheDocument();
      expect(container.querySelector('[data-key="reason-not-allowed-to-submit"]')).toHaveTextContent('Contest is over');
    });
  });

  describe('when it is allowed to submit', () => {
    describe('when there is no grading language restriction', () => {
      it('shows the preferred grading language as default value in the dropdown', async () => {
        const { container } = await renderComponent();
        expect(container.querySelector('[data-key="gradingLanguage"]')).toHaveTextContent('C++11');
      });
    });

    describe('when there is grading language restriction', () => {
      beforeEach(() => {
        gradingLanguageRestriction = { allowedLanguageNames: ['Pascal', 'Python3'] };
      });

      it('shows the first grading language as default value in the dropdown', async () => {
        const { container } = await renderComponent();
        expect(container.querySelector('[data-key="gradingLanguage"]')).toHaveTextContent('Pascal');
      });
    });
  });

  it('shows the correct form inputs', async () => {
    const { container } = await renderComponent();
    expect(container.querySelector('input[name="sourceFiles.encoder"]')).toBeInTheDocument();
    expect(container.querySelector('input[name="sourceFiles.decoder"]')).toBeInTheDocument();
    expect(container.querySelector('[data-key="gradingLanguage"]')).toHaveTextContent('C++11');
  });

  it('shows the submission warning', async () => {
    const { container } = await renderComponent();
    expect(container.querySelector('[data-key="submission-warning"]')).toHaveTextContent('Submission Warning');
  });

  describe('when the grading engine is OutputOnly', () => {
    beforeEach(() => {
      gradingEngine = 'OutputOnly';
    });

    it('does not show the grading language input', async () => {
      const { container } = await renderComponent();
      expect(container.querySelector('[name="gradingLanguage"]')).not.toBeInTheDocument();
    });
  });

  describe('when the grading engine is OutputOnlyWithSubtasks', () => {
    beforeEach(() => {
      gradingEngine = 'OutputOnlyWithSubtasks';
    });

    it('does not show the grading language input', async () => {
      const { container } = await renderComponent();
      expect(container.querySelector('[name="gradingLanguage"]')).not.toBeInTheDocument();
    });
  });
});
