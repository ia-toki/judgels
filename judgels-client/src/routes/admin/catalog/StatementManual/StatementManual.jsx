import { Button, Classes, Dialog, HTMLTable } from '@blueprintjs/core';
import { useState } from 'react';

import { ActionButtons } from '../../../../components/ActionButtons/ActionButtons';
import RichStatementText from '../../../../components/RichStatementText/RichStatementText';

const spoilerExample = `<div class="spoiler">Spoiler title
    <div class="spoiler-content">
        Spoiler content
    </div>
</div>`;

const latexSyntaxExamples = [
  { code: String.raw`Inline formula: $A \le B_i \le C^2$` },
  {
    code: String.raw`Block formula: \[A \le B_i \le C^2\]`,
    note: '(The entire formula must be in the same line.)',
  },
];

const latexExamples = [
  { code: String.raw`$1 + \frac{22}{7} + \displaystyle\frac{22}{7}$` },
  { code: String.raw`$\begin{bmatrix} 0 & 1 & 1 \\ 1 & 0 & 1 \\ 1 & 0 & 0 \end{bmatrix}$` },
  { code: String.raw`$A = (B \times C) \bmod M$` },
  { code: String.raw`\[\begin{aligned} A & = 1 + 1 \\ & = 2 \end{aligned}\]` },
  { code: String.raw`\[A = \max \begin{cases} B & (\text{case } 1) \\ C + 1 & (\text{case } 2)\end{cases}\]` },
];

function escapeHtml(text) {
  return text.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
}

function LatexExamplesTable({ examples }) {
  return (
    <HTMLTable bordered style={{ width: '100%' }}>
      <tbody>
        {examples.map(({ code, note }) => (
          <tr key={code}>
            <td style={{ width: '60%' }}>
              <code>{code}</code>
              {note && (
                <>
                  <br />
                  {note}
                </>
              )}
            </td>
            <td>
              <RichStatementText>{`<p>${escapeHtml(code)}</p>`}</RichStatementText>
            </td>
          </tr>
        ))}
      </tbody>
    </HTMLTable>
  );
}

export function StatementManual() {
  const [openManual, setOpenManual] = useState(undefined);

  const closeManual = () => setOpenManual(undefined);

  const renderFooter = () => (
    <div className={Classes.DIALOG_FOOTER}>
      <div className={Classes.DIALOG_FOOTER_ACTIONS}>
        <Button text="Close" onClick={closeManual} />
      </div>
    </div>
  );

  return (
    <>
      <ActionButtons>
        <Button small text="Formatting manual" onClick={() => setOpenManual('formatting')} />
        <Button small text="LaTeX manual" onClick={() => setOpenManual('latex')} />
      </ActionButtons>

      <Dialog isOpen={openManual === 'formatting'} onClose={closeManual} title="Formatting manual">
        <div className={Classes.DIALOG_BODY}>
          <h4>Inserting PDF</h4>
          <ul>
            <li>Upload the PDF as a media file.</li>
            <li>Switch to Source mode.</li>
            <li>
              Add it as <code>{'<embed src="render/<filename>" type="application/pdf"></embed>'}</code>.
            </li>
          </ul>
          <hr />
          <h4>Inserting image</h4>
          <ul>
            <li>Upload the image as a media file.</li>
            <li>Switch to Source mode.</li>
            <li>
              Add it as <code>{'<img src="render/<filename>">'}</code>.
            </li>
          </ul>
          <hr />
          <h4>Inserting spoiler</h4>
          <ul>
            <li>Switch to Source mode.</li>
            <li>Add it as:</li>
          </ul>
          <pre>{spoilerExample}</pre>
        </div>
        {renderFooter()}
      </Dialog>

      <Dialog isOpen={openManual === 'latex'} onClose={closeManual} title="LaTeX manual" style={{ width: '800px' }}>
        <div className={Classes.DIALOG_BODY}>
          <p>
            LaTeX rendering is powered by <a href="https://katex.org">KaTeX</a>.
          </p>
          <hr />
          <h4>Syntax</h4>
          <LatexExamplesTable examples={latexSyntaxExamples} />
          <h4>Examples</h4>
          <LatexExamplesTable examples={latexExamples} />
        </div>
        {renderFooter()}
      </Dialog>
    </>
  );
}
