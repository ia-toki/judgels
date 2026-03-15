import { act, render, screen } from '@testing-library/react';
import { vi } from 'vitest';

import { TestRouter } from '../../../../test/RouterWrapper';
import { ProblemSubmissionCard } from './ProblemSubmissionCard';

describe('ProblemSubmissionCard', () => {
  const renderComponent = async ({
    gradingEngine = 'Batch',
    gradingLanguageRestriction = { allowedLanguageNames: [] },
    onSubmit = vi.fn(),
    reasonNotAllowedToSubmit,
    submissionWarning = 'Submission Warning',
    preferredGradingLanguage = 'Cpp11',
  } = {}) => {
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

  test('when not allowed to submit, does not render the form and renders the reason', async () => {
    const { container } = await renderComponent({ reasonNotAllowedToSubmit: 'Contest is over' });
    expect(screen.queryByRole('form')).not.toBeInTheDocument();
    expect(container.querySelector('[data-key="reason-not-allowed-to-submit"]')).toHaveTextContent('Contest is over');
  });

  test('when no grading language restriction, renders the preferred grading language as default value in the dropdown', async () => {
    const { container } = await renderComponent();
    expect(container.querySelector('[data-key="gradingLanguage"]')).toHaveTextContent('C++11');
  });

  test('when there is grading language restriction, renders the first grading language as default value in the dropdown', async () => {
    const { container } = await renderComponent({
      gradingLanguageRestriction: { allowedLanguageNames: ['Pascal', 'Python3'] },
    });
    expect(container.querySelector('[data-key="gradingLanguage"]')).toHaveTextContent('Pascal');
  });

  test('renders the correct form inputs', async () => {
    const { container } = await renderComponent();
    expect(container.querySelector('input[name="sourceFiles.encoder"]')).toBeInTheDocument();
    expect(container.querySelector('input[name="sourceFiles.decoder"]')).toBeInTheDocument();
    expect(container.querySelector('[data-key="gradingLanguage"]')).toHaveTextContent('C++11');
  });

  test('renders the submission warning', async () => {
    const { container } = await renderComponent();
    expect(container.querySelector('[data-key="submission-warning"]')).toHaveTextContent('Submission Warning');
  });

  test('when grading engine is OutputOnly, does not render the grading language input', async () => {
    const { container } = await renderComponent({ gradingEngine: 'OutputOnly' });
    expect(container.querySelector('[name="gradingLanguage"]')).not.toBeInTheDocument();
  });

  test('when grading engine is OutputOnlyWithSubtasks, does not render the grading language input', async () => {
    const { container } = await renderComponent({ gradingEngine: 'OutputOnlyWithSubtasks' });
    expect(container.querySelector('[name="gradingLanguage"]')).not.toBeInTheDocument();
  });
});
