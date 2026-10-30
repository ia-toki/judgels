import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { vi } from 'vitest';

import LanguageSelect from './LanguageSelect';

describe('LanguageSelect', () => {
  test('applies a language as soon as it is selected', async () => {
    const onChange = vi.fn();
    const user = userEvent.setup();
    render(<LanguageSelect value="id" languages={['en', 'id']} onChange={onChange} />);

    await user.click(screen.getByRole('button', { name: /Indonesian/ }));
    await user.click(await screen.findByRole('menuitem', { name: /English/ }));

    expect(onChange.mock.calls[0][0]).toBe('en');
  });

  test('is disabled when there is only one language', () => {
    render(<LanguageSelect value="id" languages={['id']} onChange={vi.fn()} />);

    expect(screen.getByRole('button', { name: /Indonesian/ })).toBeDisabled();
  });
});
