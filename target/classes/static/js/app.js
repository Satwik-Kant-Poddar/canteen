document.addEventListener('DOMContentLoaded', () => {
    // Initialize Lucide Icons
    lucide.createIcons();

    // DOM Elements
    const qrForm = document.getElementById('qr-form');
    const qrText = document.getElementById('qr-text');
    const charCount = document.querySelector('.char-count');
    
    const fgColorInput = document.getElementById('fg-color');
    const bgColorInput = document.getElementById('bg-color');
    const fgColorText = fgColorInput.nextElementSibling;
    const bgColorText = bgColorInput.nextElementSibling;
    const presetBtns = document.querySelectorAll('.preset-btn');
    
    const qrSize = document.getElementById('qr-size');
    const qrFormat = document.getElementById('qr-format');
    const qrEcl = document.getElementById('qr-ecl');
    
    const generateBtn = document.getElementById('generate-btn');
    const btnText = generateBtn.querySelector('.btn-text');
    const btnLoader = generateBtn.querySelector('.btn-loader');
    
    const qrPlaceholder = document.getElementById('qr-placeholder');
    const qrResultContainer = document.getElementById('qr-result-container');
    const qrImage = document.getElementById('qr-image');
    
    const downloadBtn = document.getElementById('download-btn');
    const shareBtn = document.getElementById('share-btn');
    
    const historyList = document.getElementById('history-list');
    const clearHistoryBtn = document.getElementById('clear-history-btn');

    // Current State
    let currentQrBlobUrl = null;
    let history = JSON.parse(localStorage.getItem('qr_studio_history') || '[]');

    // Initialize Page
    updateHistoryUI();
    updateCharCount();

    // Textarea Character Counter
    qrText.addEventListener('input', updateCharCount);

    function updateCharCount() {
        const length = qrText.value.length;
        charCount.textContent = `${length} / 2000`;
        if (length > 1800) {
            charCount.style.color = '#ef4444';
        } else {
            charCount.style.color = 'var(--text-muted)';
        }
    }

    // Color Pickers
    fgColorInput.addEventListener('input', (e) => {
        const val = e.target.value.toUpperCase();
        fgColorText.textContent = val;
        clearActivePreset();
    });

    bgColorInput.addEventListener('input', (e) => {
        const val = e.target.value.toUpperCase();
        bgColorText.textContent = val;
        clearActivePreset();
    });

    function clearActivePreset() {
        presetBtns.forEach(btn => btn.classList.remove('active'));
    }

    // Presets
    presetBtns.forEach(btn => {
        btn.addEventListener('click', () => {
            clearActivePreset();
            btn.classList.add('active');
            
            const fg = btn.dataset.fg;
            const bg = btn.dataset.bg;
            
            fgColorInput.value = fg;
            fgColorText.textContent = fg.toUpperCase();
            
            bgColorInput.value = bg;
            bgColorText.textContent = bg.toUpperCase();
        });
    });

    // Form Submit (Generate)
    qrForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const text = qrText.value.trim();
        if (!text) return;

        const fgColor = fgColorInput.value;
        const bgColor = bgColorInput.value;
        const size = qrSize.value;
        const format = qrFormat.value;
        const ecl = qrEcl.value;

        // Set Loading State
        generateBtn.disabled = true;
        btnText.textContent = 'Generating...';
        btnLoader.classList.remove('hidden');

        try {
            const queryParams = new URLSearchParams({
                text: text,
                width: size,
                height: size,
                ecl: ecl,
                format: format,
                onColor: fgColor,
                offColor: bgColor
            });

            const apiUrl = `/api/qr/generate?${queryParams.toString()}`;
            const response = await fetch(apiUrl);
            
            if (!response.ok) {
                throw new Error('Failed to generate QR Code. Make sure the server is running.');
            }

            const blob = await response.blob();
            
            // Clean up previous blob URL if exists
            if (currentQrBlobUrl) {
                URL.revokeObjectURL(currentQrBlobUrl);
            }
            
            currentQrBlobUrl = URL.createObjectURL(blob);
            qrImage.src = currentQrBlobUrl;
            
            // Show result view
            qrPlaceholder.classList.add('hidden');
            qrResultContainer.classList.remove('hidden');

            // Add to history
            addToHistory({
                id: Date.now(),
                text: text,
                fgColor: fgColor,
                bgColor: bgColor,
                size: size,
                format: format,
                ecl: ecl,
                timestamp: new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
            });

        } catch (error) {
            console.error(error);
            alert(error.message || 'Something went wrong while generating QR code.');
        } finally {
            // Restore button state
            generateBtn.disabled = false;
            btnText.textContent = 'Generate QR Code';
            btnLoader.classList.add('hidden');
        }
    });

    // Download Button Action
    downloadBtn.addEventListener('click', () => {
        if (!currentQrBlobUrl) return;
        const format = qrFormat.value;
        const filename = `qr-code-${Date.now()}.${format}`;
        
        const a = document.createElement('a');
        a.href = currentQrBlobUrl;
        a.download = filename;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
    });

    // Share Button Action (Copy direct API Url)
    shareBtn.addEventListener('click', () => {
        const text = qrText.value.trim();
        const fgColor = fgColorInput.value;
        const bgColor = bgColorInput.value;
        const size = qrSize.value;
        const format = qrFormat.value;
        const ecl = qrEcl.value;

        const queryParams = new URLSearchParams({
            text: text,
            width: size,
            height: size,
            ecl: ecl,
            format: format,
            onColor: fgColor,
            offColor: bgColor
        });

        const fullUrl = `${window.location.origin}/api/qr/generate?${queryParams.toString()}`;
        
        navigator.clipboard.writeText(fullUrl).then(() => {
            const originalHtml = shareBtn.innerHTML;
            shareBtn.innerHTML = `<i data-lucide="check"></i> Copied!`;
            lucide.createIcons();
            
            setTimeout(() => {
                shareBtn.innerHTML = originalHtml;
                lucide.createIcons();
            }, 2000);
        }).catch(err => {
            console.error('Could not copy text: ', err);
        });
    });

    // History management
    function addToHistory(item) {
        // Prevent duplicate consecutive entries with same text/settings
        const lastItem = history[0];
        if (lastItem && 
            lastItem.text === item.text && 
            lastItem.fgColor === item.fgColor && 
            lastItem.bgColor === item.bgColor &&
            lastItem.size === item.size &&
            lastItem.format === item.format &&
            lastItem.ecl === item.ecl) {
            return;
        }

        // Limit history to 10 items
        history.unshift(item);
        if (history.length > 10) {
            history.pop();
        }

        localStorage.setItem('qr_studio_history', JSON.stringify(history));
        updateHistoryUI();
    }

    function updateHistoryUI() {
        if (history.length === 0) {
            historyList.innerHTML = '<p class="empty-history-text">No QR codes generated yet in this session.</p>';
            clearHistoryBtn.classList.add('hidden');
            return;
        }

        clearHistoryBtn.classList.remove('hidden');
        historyList.innerHTML = '';

        history.forEach(item => {
            const itemEl = document.createElement('div');
            itemEl.className = 'history-item';
            
            // Build tiny thumbnail URL
            const thumbParams = new URLSearchParams({
                text: item.text,
                width: '100',
                height: '100',
                ecl: 'L',
                format: 'png',
                onColor: item.fgColor,
                offColor: item.bgColor
            });
            const thumbUrl = `/api/qr/generate?${thumbParams.toString()}`;

            itemEl.innerHTML = `
                <div class="history-item-details">
                    <div class="history-item-thumb">
                        <img src="${thumbUrl}" alt="Thumbnail">
                    </div>
                    <div class="history-item-info">
                        <div class="history-item-text">${escapeHtml(item.text)}</div>
                        <div class="history-item-meta">${item.size}x${item.size} • ${item.format.toUpperCase()} • ${item.timestamp}</div>
                    </div>
                </div>
                <div class="history-item-actions">
                    <button type="button" class="history-action-btn delete-btn" title="Delete">
                        <i data-lucide="trash-2"></i>
                    </button>
                </div>
            `;

            // Load history item on click
            itemEl.addEventListener('click', (e) => {
                // If clicked on delete button, do not load
                if (e.target.closest('.delete-btn')) {
                    e.stopPropagation();
                    deleteHistoryItem(item.id);
                    return;
                }
                loadHistoryItem(item);
            });

            historyList.appendChild(itemEl);
        });

        lucide.createIcons();
    }

    function deleteHistoryItem(id) {
        history = history.filter(item => item.id !== id);
        localStorage.setItem('qr_studio_history', JSON.stringify(history));
        updateHistoryUI();
    }

    clearHistoryBtn.addEventListener('click', () => {
        history = [];
        localStorage.removeItem('qr_studio_history');
        updateHistoryUI();
    });

    function loadHistoryItem(item) {
        qrText.value = item.text;
        updateCharCount();
        
        fgColorInput.value = item.fgColor;
        fgColorText.textContent = item.fgColor.toUpperCase();
        
        bgColorInput.value = item.bgColor;
        bgColorText.textContent = item.bgColor.toUpperCase();
        
        qrSize.value = item.size;
        qrFormat.value = item.format;
        qrEcl.value = item.ecl;

        // Sync active preset selection
        clearActivePreset();
        presetBtns.forEach(btn => {
            if (btn.dataset.fg === item.fgColor && btn.dataset.bg === item.bgColor) {
                btn.classList.add('active');
            }
        });

        // Trigger form submit to render/preview
        qrForm.dispatchEvent(new Event('submit'));
    }

    function escapeHtml(text) {
        const map = {
            '&': '&amp;',
            '<': '&lt;',
            '>': '&gt;',
            '"': '&quot;',
            "'": '&#039;'
        };
        return text.replace(/[&<>"']/g, function(m) { return map[m]; });
    }
});
