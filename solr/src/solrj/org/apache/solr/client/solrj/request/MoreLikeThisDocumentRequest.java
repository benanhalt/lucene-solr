/* This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
/**
 * 
 */
package org.apache.solr.client.solrj.request;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrResponse;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.params.MoreLikeThisParams;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.ContentStream;

/**
 * @author ben
 *
 * @code_status Alpha
 *
 * Created Date: Mar 30, 2011
 *
 */
public class MoreLikeThisDocumentRequest extends SolrRequest
{
    private List<SolrInputDocument> documents = new LinkedList<SolrInputDocument>();
    private SolrParams params;

    public MoreLikeThisDocumentRequest(SolrParams params)
    {
        super(METHOD.GET, null);
        this.params = params;
    }
    
    @Override
    public String getPath()
    {
        String qt = params.get(CommonParams.QT);
        if (qt == null)
        {
            qt = "/" + MoreLikeThisParams.MLT;
        }
        return qt;
    }

    /* (non-Javadoc)
     * @see org.apache.solr.client.solrj.SolrRequest#getParams()
     */
    @Override
    public SolrParams getParams()
    {
        return params;
    }

    /* (non-Javadoc)
     * @see org.apache.solr.client.solrj.SolrRequest#getContentStreams()
     */
    @Override
    public Collection<ContentStream> getContentStreams() throws IOException
    {
        StringWriter writer = new StringWriter();
        writer.write("<docs>");
        for (SolrInputDocument document : documents) {
          ClientUtils.writeXML(document, writer);
        }
        writer.write("</docs>");
        writer.flush();

        String xml = writer.toString();
        return ClientUtils.toContentStreams((xml.length() > 0) ? xml : null, 
                ClientUtils.TEXT_XML);
    }

    /* (non-Javadoc)
     * @see org.apache.solr.client.solrj.SolrRequest#process(org.apache.solr.client.solrj.SolrServer)
     */
    @Override
    public QueryResponse process(SolrServer server) throws SolrServerException, IOException
    {
        try {
            long startTime = System.currentTimeMillis();
            QueryResponse res = new QueryResponse( server.request( this ), server );
            res.setElapsedTime( System.currentTimeMillis()-startTime );
            return res;
          } catch (SolrServerException e){
            throw e;
          } catch (Exception e) {
            throw new SolrServerException("Error executing query", e);
          }
    }

    /**
     * Adds a document to be analyzed.
     *
     * @param doc The document to be analyzed.
     *
     * @return This DocumentAnalysisRequest (fluent interface support).
     */
    public MoreLikeThisDocumentRequest addDocument(SolrInputDocument doc) {
      documents.add(doc);
      return this;
    }

    /**
     * Adds a collection of documents to be analyzed.
     *
     * @param docs The documents to be analyzed.
     *
     * @return This DocumentAnalysisRequest (fluent interface support).
     *
     * @see #addDocument(org.apache.solr.common.SolrInputDocument)
     */
    public MoreLikeThisDocumentRequest addDocuments(Collection<SolrInputDocument> docs) {
      documents.addAll(docs);
      return this;
    }   
}
