package lib.bindable;

import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class BindableEditTextTest
{
    @Mock
    MockScopeContext contextMock;
    @Mock
    AttributeSet     attributeSetMock;
    @Mock
    View             viewMock;
    @Mock
    KeyEvent         keyEventMock;

    @Before
    public void setup()
    {
        contextMock = mock(MockScopeContext.class);
        attributeSetMock = mock(AttributeSet.class);
        when(attributeSetMock.getAttributeValue(anyString(),anyString())).thenReturn("test");
        when(attributeSetMock.getAttributeCount()).thenReturn(1);
        when(attributeSetMock.getAttributeName(anyInt())).thenReturn("test");

        when(contextMock.getModel(anyString())).thenReturn("test");

        viewMock = mock(View.class);
        keyEventMock = mock(KeyEvent.class);
    }

    @Test
    public void getExportableAttributes()
    {
        BindableEditText underTest = new BindableEditText(contextMock, attributeSetMock);
        AttributeSet map = underTest.getExportableAttributes();
        assertNotNull(map);
        assertTrue(map.getAttributeCount() == 1);
    }

    @Test
    public void getScope()
    {
        BindableEditText underTest = new BindableEditText(contextMock, attributeSetMock);
        assertEquals(contextMock,underTest.getScope());
    }

    @Test
    public void modelChanged()
    {
        BindableEditText underTest = spy(new BindableEditText(contextMock, attributeSetMock));

        underTest.modelChanged(contextMock);
        verify(underTest).setText(anyString());
    }

    @Test
    public void test_onClick()
    {
        BindableEditText underTest = spy(new BindableEditText(contextMock, attributeSetMock));

        underTest.onClick(viewMock);
        verify(underTest).getExportableAttributes();
    }

    @Test
    public void test_onKey()
    {
        BindableEditText underTest = new BindableEditText(contextMock, attributeSetMock);

        assertFalse(underTest.onKey(viewMock,0,keyEventMock));
    }

    @Test
    public void test_onLongClick()
    {
        BindableEditText underTest = new BindableEditText(contextMock, attributeSetMock);

        assertFalse(underTest.onLongClick(viewMock));
    }
}